package com.rin.novel.service;

import com.rin.novel.core.common.resp.RestResp;
import com.rin.novel.dto.resp.CheckinStatusRespDto;
import com.rin.novel.dto.resp.UserCouponRespDto;

import java.util.List;

/**
 * 打卡活动 服务类
 *
 * @author zhim00
 */
public interface CheckinActivityService {

    /**
     * 上报阅读时长
     *
     * @param userId          用户ID
     * @param durationSeconds 阅读时长(秒)
     * @return 上报结果，包含今日累计时长和是否触发打卡
     */
    RestResp<Void> reportReading(Long userId, Integer durationSeconds);

    /**
     * 查询打卡活动状态
     *
     * @param userId 用户ID
     * @return 打卡状态
     */
    RestResp<CheckinStatusRespDto> getCheckinStatus(Long userId);

    /**
     * 抢券
     *
     * @param userId 用户ID
     * @return 抢券结果
     */
    RestResp<Void> grabCoupon(Long userId);

    /**
     * 查询用户优惠券列表
     *
     * @param userId 用户ID
     * @return 优惠券列表
     */
    RestResp<List<UserCouponRespDto>> listUserCoupons(Long userId);
}

