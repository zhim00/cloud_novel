package com.rin.novel.core.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.rin.novel.core.constant.CacheConsts;
import com.rin.novel.dao.entity.UserCheckinRecord;
import com.rin.novel.dao.entity.UserCoupon;
import com.rin.novel.dao.entity.UserDailyReading;
import com.rin.novel.dao.mapper.UserCheckinRecordMapper;
import com.rin.novel.dao.mapper.UserCouponMapper;
import com.rin.novel.dao.mapper.UserDailyReadingMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * 打卡活动定时任务
 *
 * @author zhim00
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CheckinActivityTask {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserCheckinRecordMapper userCheckinRecordMapper;
    private final UserDailyReadingMapper userDailyReadingMapper;
    private final UserCouponMapper userCouponMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 每日优惠券库存
     */
    private static final int DAILY_COUPON_STOCK = 100;

    /**
     * 每日0点初始化当日优惠券库存
     * XXL-JOB方式，需要在XXL-JOB后台配置
     */
    @XxlJob("initDailyCouponStockJobHandler")
    public ReturnT<String> initDailyCouponStock() {
        try {
            doInitDailyCouponStock();
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("初始化当日优惠券库存失败", e);
            return ReturnT.FAIL;
        }
    }

    /**
     * 每日0点初始化当日优惠券库存 - Spring Scheduled方式(备用)
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void initDailyCouponStockScheduled() {
        doInitDailyCouponStock();
    }

    private void doInitDailyCouponStock() {
        String today = LocalDate.now().format(DATE_FORMATTER);
        String stockKey = CacheConsts.CHECKIN_COUPON_STOCK_CACHE_KEY + today;

        // 设置库存，有效期25小时（确保覆盖一整天+缓冲）
        stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(DAILY_COUPON_STOCK), Duration.ofHours(25));

        log.info("初始化{}优惠券库存成功，库存数量：{}", today, DAILY_COUPON_STOCK);
    }

    /**
     * 每月1号0点重置活动数据
     * XXL-JOB方式
     */
    @XxlJob("resetMonthlyActivityJobHandler")
    public ReturnT<String> resetMonthlyActivity() {
        try {
            doResetMonthlyActivity();
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("重置月度活动数据失败", e);
            return ReturnT.FAIL;
        }
    }

    /**
     * 每月1号0点重置活动数据 - Spring Scheduled方式(备用)
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlyActivityScheduled() {
        doResetMonthlyActivity();
    }

    private void doResetMonthlyActivity() {
        // 获取上个月份
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        String lastMonthStr = lastMonth.format(MONTH_FORMATTER);
        LocalDate lastMonthStart = lastMonth.atDay(1);
        LocalDate lastMonthEnd = lastMonth.atEndOfMonth();

        log.info("开始重置{}月度活动数据", lastMonthStr);

        // 1. 删除上月打卡记录
        QueryWrapper<UserCheckinRecord> checkinQuery = new QueryWrapper<>();
        checkinQuery.eq("checkin_month", lastMonthStr);
        int deletedCheckins = userCheckinRecordMapper.delete(checkinQuery);
        log.info("删除上月打卡记录{}条", deletedCheckins);

        // 2. 删除上月每日阅读记录
        QueryWrapper<UserDailyReading> readingQuery = new QueryWrapper<>();
        readingQuery.between("reading_date", lastMonthStart, lastMonthEnd);
        int deletedReadings = userDailyReadingMapper.delete(readingQuery);
        log.info("删除上月阅读记录{}条", deletedReadings);

        // 3. 清理Redis中上月相关缓存
        cleanLastMonthRedisKeys(lastMonthStr);

        log.info("重置{}月度活动数据完成", lastMonthStr);
    }

    /**
     * 清理上月Redis缓存
     */
    private void cleanLastMonthRedisKeys(String lastMonthStr) {
        // 清理上月打卡天数缓存
        String countPattern = CacheConsts.CHECKIN_MONTH_COUNT_CACHE_KEY + lastMonthStr + "::*";
        deleteKeysByPattern(countPattern);

        // 清理上月已领券标记缓存
        String grabbedPattern = CacheConsts.CHECKIN_USER_GRABBED_CACHE_KEY + lastMonthStr + "::*";
        deleteKeysByPattern(grabbedPattern);

        log.info("清理上月{}Redis缓存完成", lastMonthStr);
    }

    private void deleteKeysByPattern(String pattern) {
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
            log.info("删除缓存Key数量：{}，pattern：{}", keys.size(), pattern);
        }
    }

    /**
     * 每日检查并更新过期优惠券状态
     * XXL-JOB方式
     */
    @XxlJob("expireCouponJobHandler")
    public ReturnT<String> expireCoupons() {
        try {
            doExpireCoupons();
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("更新过期优惠券状态失败", e);
            return ReturnT.FAIL;
        }
    }

    /**
     * 每日1点检查并更新过期优惠券状态 - Spring Scheduled方式(备用)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void expireCouponsScheduled() {
        doExpireCoupons();
    }

    private void doExpireCoupons() {
        LocalDateTime now = LocalDateTime.now();

        // 更新过期优惠券状态为已过期(status=2)，只更新未使用的
        UpdateWrapper<UserCoupon> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", 2)
                .set("update_time", now)
                .eq("status", 0)
                .lt("expire_time", now);

        int updated = userCouponMapper.update(null, updateWrapper);
        log.info("更新过期优惠券数量：{}", updated);
    }
}


