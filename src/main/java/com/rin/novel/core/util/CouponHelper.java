package com.rin.novel.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 优惠券计算工具类
 *
 * @author zhim00
 */
public final class CouponHelper {

    private CouponHelper() {
        // 私有构造函数，防止实例化
    }

    /**
     * 计算折扣后金额
     *
     * @param originalAmount 原始金额(分)
     * @param discountRate   折扣率(85表示85折)
     * @return 折扣后金额(分)
     */
    public static int calculateDiscountAmount(int originalAmount, int discountRate) {
        if (discountRate <= 0 || discountRate >= 100) {
            return originalAmount;
        }
        BigDecimal original = BigDecimal.valueOf(originalAmount);
        BigDecimal rate = BigDecimal.valueOf(discountRate).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return original.multiply(rate).setScale(0, RoundingMode.DOWN).intValue();
    }

    /**
     * 计算优惠金额
     *
     * @param originalAmount 原始金额(分)
     * @param discountRate   折扣率(85表示85折)
     * @return 优惠金额(分)
     */
    public static int calculateSavedAmount(int originalAmount, int discountRate) {
        return originalAmount - calculateDiscountAmount(originalAmount, discountRate);
    }

    /**
     * 检查是否满足最低消费门槛
     *
     * @param amount    消费金额(分)
     * @param minAmount 最低消费金额(分)，0表示无门槛
     * @return 是否满足
     */
    public static boolean meetsMinAmount(int amount, int minAmount) {
        return minAmount <= 0 || amount >= minAmount;
    }

    /**
     * 格式化折扣率为描述文字
     *
     * @param discountRate 折扣率(85表示85折)
     * @return 描述文字，如"8.5折"
     */
    public static String formatDiscountRate(int discountRate) {
        if (discountRate % 10 == 0) {
            return (discountRate / 10) + "折";
        }
        return BigDecimal.valueOf(discountRate)
                .divide(BigDecimal.valueOf(10), 1, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString() + "折";
    }

    /**
     * 格式化金额为元
     *
     * @param amountInFen 金额(分)
     * @return 金额(元)字符串
     */
    public static String formatAmountToYuan(int amountInFen) {
        return BigDecimal.valueOf(amountInFen)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }
}

