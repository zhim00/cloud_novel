package com.rin.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rin.novel.core.annotation.Key;
import com.rin.novel.core.annotation.Lock;
import com.rin.novel.core.common.constant.ErrorCodeEnum;
import com.rin.novel.core.common.exception.BusinessException;
import com.rin.novel.core.common.resp.RestResp;
import com.rin.novel.core.util.CouponHelper;
import com.rin.novel.dao.entity.CouponTemplate;
import com.rin.novel.dao.entity.UserCheckinRecord;
import com.rin.novel.dao.entity.UserCoupon;
import com.rin.novel.dao.entity.UserDailyReading;
import com.rin.novel.dao.mapper.CouponTemplateMapper;
import com.rin.novel.dao.mapper.UserCheckinRecordMapper;
import com.rin.novel.dao.mapper.UserCouponMapper;
import com.rin.novel.dao.mapper.UserDailyReadingMapper;
import com.rin.novel.dto.resp.CheckinStatusRespDto;
import com.rin.novel.dto.resp.UserCouponRespDto;
import com.rin.novel.manager.redis.CheckinActivityRedisManager;
import com.rin.novel.service.CheckinActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 打卡活动 服务实现类
 *
 * @author zhim00
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CheckinActivityServiceImpl implements CheckinActivityService {

    private final CheckinActivityRedisManager checkinActivityRedisManager;
    private final UserDailyReadingMapper userDailyReadingMapper;
    private final UserCheckinRecordMapper userCheckinRecordMapper;
    private final UserCouponMapper userCouponMapper;
    private final CouponTemplateMapper couponTemplateMapper;

    /**
     * 打卡所需阅读时长(秒) - 30分钟
     */
    private static final int CHECKIN_REQUIRED_SECONDS = 30 * 60;

    /**
     * 抢券所需打卡天数
     */
    private static final int GRAB_REQUIRED_DAYS = 21;

    /**
     * 打卡活动优惠券类型编码
     */
    private static final String CHECKIN_COUPON_TYPE = "CHECKIN_21_DAYS";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResp<Void> reportReading(Long userId, Integer durationSeconds) {
        LocalDate today = LocalDate.now();
        String currentMonth = today.format(MONTH_FORMATTER);

        // 检查用户当月是否已领券，已领券则关闭入口
        if (hasGrabbedCouponThisMonth(userId, currentMonth)) {
            throw new BusinessException(ErrorCodeEnum.CHECKIN_ENTRY_CLOSED);
        }

        // Redis原子累加阅读时长
        long totalSeconds = checkinActivityRedisManager.incrementDailyReading(userId, today, durationSeconds);

        // 同步更新数据库
        updateDailyReadingInDb(userId, today, (int) totalSeconds);

        // 检查是否达到打卡条件且今日未打卡
        if (totalSeconds >= CHECKIN_REQUIRED_SECONDS && !isTodayCheckedIn(userId, today)) {
            // 自动打卡
            doCheckin(userId, today, currentMonth);
            String dateStr = today.format(DATE_FORMATTER);
            log.info("用户{}在{}自动打卡成功", userId, dateStr);
        }

        return RestResp.ok();
    }

    @Override
    public RestResp<CheckinStatusRespDto> getCheckinStatus(Long userId) {
        LocalDate today = LocalDate.now();
        String currentMonth = today.format(MONTH_FORMATTER);

        // 检查是否已领券
        boolean couponGrabbed = hasGrabbedCouponThisMonth(userId, currentMonth);

        // 获取当月打卡天数
        int checkinDays = getMonthlyCheckinDays(userId, currentMonth);

        // 获取今日阅读时长
        int todayReadingSeconds = checkinActivityRedisManager.getDailyReadingSeconds(userId, today);

        // 今日是否已打卡
        boolean todayCheckedIn = isTodayCheckedIn(userId, today);

        // 是否有资格抢券
        boolean canGrabCoupon = checkinDays >= GRAB_REQUIRED_DAYS && !couponGrabbed;

        // 打卡入口状态和关闭提示
        String closeMessage = couponGrabbed ? "已成功领券，快去使用吧" : null;

        return RestResp.ok(CheckinStatusRespDto.builder()
                .checkinDays(checkinDays)
                .todayCheckedIn(todayCheckedIn)
                .todayReadingSeconds(todayReadingSeconds)
                .couponGrabbed(couponGrabbed)
                .canGrabCoupon(canGrabCoupon)
                .entryClosed(couponGrabbed)
                .closeMessage(closeMessage)
                .build());
    }

    @Override
    @Lock(prefix = "grabCoupon", isWait = true, waitTime = 3, failCode = ErrorCodeEnum.CHECKIN_GRAB_FAILED)
    @Transactional(rollbackFor = Exception.class)
    public RestResp<Void> grabCoupon(@Key Long userId) {
        LocalDate today = LocalDate.now();
        String currentMonth = today.format(MONTH_FORMATTER);

        // 1. 检查是否已领券
        if (hasGrabbedCouponThisMonth(userId, currentMonth)) {
            throw new BusinessException(ErrorCodeEnum.CHECKIN_COUPON_ALREADY_GRABBED);
        }

        // 2. 检查打卡天数是否满足
        int checkinDays = getMonthlyCheckinDays(userId, currentMonth);
        if (checkinDays < GRAB_REQUIRED_DAYS) {
            throw new BusinessException(ErrorCodeEnum.CHECKIN_DAYS_NOT_ENOUGH);
        }

        // 3. 获取优惠券模板
        CouponTemplate template = getCouponTemplate(CHECKIN_COUPON_TYPE);

        // 4. Redis扣减库存
        Long remainingStock = checkinActivityRedisManager.decrementCouponStock(today);

        if (remainingStock == null || remainingStock < 0) {
            // 库存不足，恢复库存
            if (remainingStock != null) {
                checkinActivityRedisManager.incrementCouponStock(today);
            }
            throw new BusinessException(ErrorCodeEnum.CHECKIN_COUPON_SOLD_OUT);
        }

        // 5. 写入优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponTemplateId(template.getId());
        userCoupon.setCouponType(template.getCouponType());
        userCoupon.setDiscountRate(template.getDiscountRate());
        userCoupon.setActivityMonth(currentMonth);
        userCoupon.setStatus(0);
        // 有效期至下个月7号0点（打卡活动特殊规则）
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        LocalDateTime expireTime = nextMonth.atDay(7).atStartOfDay();
        userCoupon.setExpireTime(expireTime);
        userCoupon.setCreateTime(LocalDateTime.now());
        userCoupon.setUpdateTime(LocalDateTime.now());
        userCouponMapper.insert(userCoupon);

        // 6. 设置已领券标记
        checkinActivityRedisManager.setUserGrabbedFlag(userId, currentMonth);

        // 7. 清除打卡天数缓存
        checkinActivityRedisManager.deleteMonthlyCheckinDays(userId, currentMonth);

        log.info("用户{}抢券成功，当月{}，剩余库存{}", userId, currentMonth, remainingStock);

        return RestResp.ok(null);
    }

    /**
     * 获取优惠券模板
     */
    private CouponTemplate getCouponTemplate(String couponType) {
        QueryWrapper<CouponTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("coupon_type", couponType)
                .eq("status", 1);
        CouponTemplate template = couponTemplateMapper.selectOne(queryWrapper);
        if (template == null) {
            log.error("优惠券模板不存在或已禁用: {}", couponType);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR);
        }
        return template;
    }

    @Override
    public RestResp<List<UserCouponRespDto>> listUserCoupons(Long userId) {
        QueryWrapper<UserCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time");
        List<UserCoupon> coupons = userCouponMapper.selectList(queryWrapper);

        List<UserCouponRespDto> result = coupons.stream().map(coupon -> {
            String statusDesc = switch (coupon.getStatus()) {
                case 0 -> "未使用";
                case 1 -> "已使用";
                case 2 -> "已过期";
                default -> "未知";
            };
            String discountDesc = CouponHelper.formatDiscountRate(coupon.getDiscountRate()) + "无门槛优惠券";
            return UserCouponRespDto.builder()
                    .id(coupon.getId())
                    .couponType(coupon.getCouponType())
                    .discountRate(coupon.getDiscountRate())
                    .discountDesc(discountDesc)
                    .activityMonth(coupon.getActivityMonth())
                    .status(coupon.getStatus())
                    .statusDesc(statusDesc)
                    .expireTime(coupon.getExpireTime())
                    .createTime(coupon.getCreateTime())
                    .build();
        }).toList();

        return RestResp.ok(result);
    }

    /**
     * 检查用户当月是否已领券
     */
    private boolean hasGrabbedCouponThisMonth(Long userId, String month) {
        // 先查Redis
        if (checkinActivityRedisManager.hasUserGrabbed(userId, month)) {
            return true;
        }

        // 再查数据库
        QueryWrapper<UserCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("activity_month", month)
                .eq("coupon_type", CHECKIN_COUPON_TYPE);
        boolean exists = userCouponMapper.selectCount(queryWrapper) > 0;

        // 如果存在则设置Redis缓存
        if (exists) {
            checkinActivityRedisManager.setUserGrabbedFlag(userId, month);
        }

        return exists;
    }

    /**
     * 获取用户当月打卡天数
     */
    private int getMonthlyCheckinDays(Long userId, String month) {
        // 先查Redis缓存
        Integer cachedCount = checkinActivityRedisManager.getMonthlyCheckinDays(userId, month);
        if (cachedCount != null) {
            return cachedCount;
        }

        // 查数据库
        Integer count = userCheckinRecordMapper.countByUserIdAndMonth(userId, month);
        int result = count != null ? count : 0;

        // 缓存结果
        checkinActivityRedisManager.setMonthlyCheckinDays(userId, month, result);

        return result;
    }

    /**
     * 检查今日是否已打卡
     */
    private boolean isTodayCheckedIn(Long userId, LocalDate today) {
        QueryWrapper<UserCheckinRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("checkin_date", today);
        return userCheckinRecordMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 更新每日阅读时长到数据库
     */
    private void updateDailyReadingInDb(Long userId, LocalDate date, int totalSeconds) {
        QueryWrapper<UserDailyReading> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("reading_date", date);
        UserDailyReading existing = userDailyReadingMapper.selectOne(queryWrapper);

        if (existing != null) {
            existing.setDurationSeconds(totalSeconds);
            existing.setUpdateTime(LocalDateTime.now());
            userDailyReadingMapper.updateById(existing);
        } else {
            UserDailyReading reading = new UserDailyReading();
            reading.setUserId(userId);
            reading.setReadingDate(date);
            reading.setDurationSeconds(totalSeconds);
            reading.setIsCheckedIn(0);
            reading.setCreateTime(LocalDateTime.now());
            reading.setUpdateTime(LocalDateTime.now());
            userDailyReadingMapper.insert(reading);
        }
    }

    /**
     * 执行打卡
     */
    private void doCheckin(Long userId, LocalDate date, String month) {
        // 插入打卡记录
        UserCheckinRecord record = new UserCheckinRecord();
        record.setUserId(userId);
        record.setCheckinDate(date);
        record.setCheckinMonth(month);
        record.setCreateTime(LocalDateTime.now());
        userCheckinRecordMapper.insert(record);

        // 更新每日阅读表的打卡状态
        QueryWrapper<UserDailyReading> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("reading_date", date);
        UserDailyReading reading = userDailyReadingMapper.selectOne(queryWrapper);
        if (reading != null) {
            reading.setIsCheckedIn(1);
            reading.setUpdateTime(LocalDateTime.now());
            userDailyReadingMapper.updateById(reading);
        }

        // 更新Redis中的打卡天数缓存
        checkinActivityRedisManager.incrementMonthlyCheckinDays(userId, month);
    }
}

