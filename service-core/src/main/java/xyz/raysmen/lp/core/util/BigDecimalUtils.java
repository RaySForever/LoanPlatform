package xyz.raysmen.lp.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimalUtils
 * 基于BigDecimal的金融工具类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.common.util
 * @date 2022/07/17 11:41
 */
public final class BigDecimalUtils {
    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    public static final BigDecimal MONTH = new BigDecimal("12");

    public static BigDecimal percentToDecimal(BigDecimal percent) {
        return percent.divide(ONE_HUNDRED, 2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal getMonthRate(BigDecimal yearRate) {
        return yearRate.divide(MONTH, 8, RoundingMode.DOWN);
    }

    public static BigDecimal getExpectAmount(BigDecimal amount, BigDecimal monthRate, BigDecimal period) {
        return amount.multiply(monthRate).multiply(period);
    }
}
