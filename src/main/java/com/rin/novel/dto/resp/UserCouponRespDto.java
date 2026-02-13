package com.rin.novel.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户优惠券响应DTO
 *
 * @author zhim00
 */
@Data
@Builder
public class UserCouponRespDto {

    /**
     * 优惠券ID
     */
    private Long id;

    /**
     * 优惠券类型
     */
    private String couponType;

    /**
     * 折扣率(85表示85折)
     */
    private Integer discountRate;

    /**
     * 折扣描述
     */
    private String discountDesc;

    /**
     * 活动月份
     */
    private String activityMonth;

    /**
     * 状态 0-未使用 1-已使用 2-已过期
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 领取时间
     */
    private LocalDateTime createTime;
}

