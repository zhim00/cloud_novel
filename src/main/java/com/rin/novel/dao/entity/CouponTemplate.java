package com.rin.novel.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 优惠券模板
 *
 * @author zhim00
 */
@Data
@TableName("coupon_template")
public class CouponTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券类型编码
     */
    private String couponType;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 折扣率(85表示85折)
     */
    private Integer discountRate;

    /**
     * 最低消费金额(分)，0表示无门槛
     */
    private Integer minAmount;

    /**
     * 优惠券描述
     */
    private String description;

    /**
     * 有效天数
     */
    private Integer expireDays;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

