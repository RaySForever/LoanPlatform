package xyz.raysmen.lp.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 一次还本还息工具类
 *
 * @author Rays
 * @date 2022-07-17
 */
public class Amount4Helper {

    /**
     * 还款金额 = 本金 + 本金*月利率*期限
     *
     * @param amount     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月偿还利息表
     */
    public static Map<Integer, BigDecimal> getPerMonthInterest(BigDecimal amount, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> map = new HashMap<>();
        BigDecimal monthRate = yearRate.divide(BigDecimalUtils.MONTH, 8, RoundingMode.HALF_UP);
        BigDecimal monthInterest = amount.multiply(monthRate).multiply(new BigDecimal(totalMonth));
        // 一次还息
        map.put(1, monthInterest);
        return map;
    }

    /**
     * 还款本金，一次还本还息
     *
     * @param amount     总借款额（贷款本金）
     * @param yearRate   年利率（无必要）
     * @param totalMonth 还款总月数（无必要）
     * @return 每月还款本金表
     */
    public static Map<Integer, BigDecimal> getPerMonthPrincipal(BigDecimal amount, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> map = new HashMap<>();
        // 一次还本
        map.put(1, amount);
        return map;
    }

    /**
     * 总利息
     *
     * @param amount     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 返回总利息
     */
    public static BigDecimal getInterestCount(BigDecimal amount, BigDecimal yearRate, int totalMonth) {
        BigDecimal monthRate = yearRate.divide(BigDecimalUtils.MONTH, 8, RoundingMode.HALF_UP);
        BigDecimal interestCount = amount.multiply(monthRate).multiply(new BigDecimal(totalMonth));
        return interestCount.setScale(8, RoundingMode.HALF_UP);
    }
}