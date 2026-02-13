package com.rin.novel.dto.resp;

import lombok.Builder;
import lombok.Data;

/**
 * 打卡活动状态响应DTO
 *
 * @author zhim00
 */
@Data
@Builder
public class CheckinStatusRespDto {

    /**
     * 当月累计打卡天数
     */
    private Integer checkinDays;

    /**
     * 今日是否已打卡
     */
    private Boolean todayCheckedIn;

    /**
     * 今日已阅读时长(秒)
     */
    private Integer todayReadingSeconds;

    /**
     * 当月是否已领取优惠券
     */
    private Boolean couponGrabbed;

    /**
     * 是否有资格抢券(满21天且未领券)
     */
    private Boolean canGrabCoupon;

    /**
     * 打卡入口是否关闭(已领券则关闭)
     */
    private Boolean entryClosed;

    /**
     * 关闭提示信息
     */
    private String closeMessage;
}

