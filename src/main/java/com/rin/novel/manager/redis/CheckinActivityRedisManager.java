package com.rin.novel.manager.redis;

import com.rin.novel.core.constant.CacheConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 打卡活动 Redis 管理类
 *
 * @author zhim00
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CheckinActivityRedisManager {

    private final StringRedisTemplate stringRedisTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 累加用户每日阅读时长
     *
     * @param userId          用户ID
     * @param date            日期
     * @param durationSeconds 增加的秒数
     * @return 累加后的总秒数
     */
    public long incrementDailyReading(Long userId, LocalDate date, int durationSeconds) {
        String dateStr = date.format(DATE_FORMATTER);
        String redisKey = CacheConsts.CHECKIN_DAILY_READING_CACHE_KEY + userId + "::" + dateStr;

        Long totalSeconds = stringRedisTemplate.opsForValue().increment(redisKey, durationSeconds);
        // 设置25小时过期（确保覆盖一整天+缓冲）
        stringRedisTemplate.expire(redisKey, Duration.ofHours(25));

        return totalSeconds != null ? totalSeconds : durationSeconds;
    }

    /**
     * 获取用户当日阅读时长
     *
     * @param userId 用户ID
     * @param date   日期
     * @return 阅读秒数
     */
    public int getDailyReadingSeconds(Long userId, LocalDate date) {
        String dateStr = date.format(DATE_FORMATTER);
        String redisKey = CacheConsts.CHECKIN_DAILY_READING_CACHE_KEY + userId + "::" + dateStr;
        String value = stringRedisTemplate.opsForValue().get(redisKey);
        return value != null ? Integer.parseInt(value) : 0;
    }

    /**
     * 扣减优惠券库存
     *
     * @param date 日期
     * @return 扣减后的剩余库存，null表示key不存在
     */
    public Long decrementCouponStock(LocalDate date) {
        String dateStr = date.format(DATE_FORMATTER);
        String stockKey = CacheConsts.CHECKIN_COUPON_STOCK_CACHE_KEY + dateStr;
        return stringRedisTemplate.opsForValue().decrement(stockKey);
    }

    /**
     * 恢复优惠券库存（扣减失败时回滚）
     *
     * @param date 日期
     */
    public void incrementCouponStock(LocalDate date) {
        String dateStr = date.format(DATE_FORMATTER);
        String stockKey = CacheConsts.CHECKIN_COUPON_STOCK_CACHE_KEY + dateStr;
        stringRedisTemplate.opsForValue().increment(stockKey);
    }

    /**
     * 设置用户当月已领券标记
     *
     * @param userId 用户ID
     * @param month  月份 yyyy-MM
     */
    public void setUserGrabbedFlag(Long userId, String month) {
        String grabbedKey = CacheConsts.CHECKIN_USER_GRABBED_CACHE_KEY + month + "::" + userId;
        stringRedisTemplate.opsForValue().set(grabbedKey, "1", Duration.ofDays(35));
    }

    /**
     * 检查用户当月是否已领券
     *
     * @param userId 用户ID
     * @param month  月份 yyyy-MM
     * @return 是否已领券
     */
    public boolean hasUserGrabbed(Long userId, String month) {
        String grabbedKey = CacheConsts.CHECKIN_USER_GRABBED_CACHE_KEY + month + "::" + userId;
        Boolean hasKey = stringRedisTemplate.hasKey(grabbedKey);
        return hasKey != null && hasKey;
    }

    /**
     * 获取用户当月打卡天数缓存
     *
     * @param userId 用户ID
     * @param month  月份 yyyy-MM
     * @return 打卡天数，null表示缓存不存在
     */
    public Integer getMonthlyCheckinDays(Long userId, String month) {
        String countKey = CacheConsts.CHECKIN_MONTH_COUNT_CACHE_KEY + month + "::" + userId;
        String countStr = stringRedisTemplate.opsForValue().get(countKey);
        return countStr != null ? Integer.parseInt(countStr) : null;
    }

    /**
     * 设置用户当月打卡天数缓存
     *
     * @param userId 用户ID
     * @param month  月份 yyyy-MM
     * @param count  打卡天数
     */
    public void setMonthlyCheckinDays(Long userId, String month, int count) {
        String countKey = CacheConsts.CHECKIN_MONTH_COUNT_CACHE_KEY + month + "::" + userId;
        stringRedisTemplate.opsForValue().set(countKey, String.valueOf(count), Duration.ofHours(2));
    }

    /**
     * 增加用户当月打卡天数缓存
     *
     * @param userId 用户ID
     * @param month  月份 yyyy-MM
     */
    public void incrementMonthlyCheckinDays(Long userId, String month) {
        String countKey = CacheConsts.CHECKIN_MONTH_COUNT_CACHE_KEY + month + "::" + userId;
        stringRedisTemplate.opsForValue().increment(countKey);
        stringRedisTemplate.expire(countKey, Duration.ofDays(35));
    }

    /**
     * 删除用户当月打卡天数缓存
     *
     * @param userId 用户ID
     * @param month  月份 yyyy-MM
     */
    public void deleteMonthlyCheckinDays(Long userId, String month) {
        String countKey = CacheConsts.CHECKIN_MONTH_COUNT_CACHE_KEY + month + "::" + userId;
        stringRedisTemplate.delete(countKey);
    }
}

