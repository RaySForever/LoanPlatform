package xyz.raysmen.lp.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 等额本息工具类
 * 校验网址：http://www.xjumc.com/
 * 等额本息是指一种贷款的还款方式，是在还款期内，每月偿还同等数额的贷款(包括本金和利息)，和等额本金是不一样的概念，虽然刚开始还款时每月还款额可能会低于等额本金还款方式，但是最终所还利息会高于等额本金还款方式，该方式经常被银行使用。
 * <p>
 * 每月还款数额计算公式如下：
 * 每月还款额=贷款本金×[月利率×(1+月利率) ^ 还款月数]÷{[(1+月利率) ^ 还款月数]-1}
 *
 * @author Rays
 * @date 2022-07-17
 */
public class Amount1Helper {

    /**
     * 每月还款利息
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月偿还利息表
     */
    public static Map<Integer, BigDecimal> getPerMonthInterest(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> map = new HashMap<>(totalMonth);
        //月利息
        BigDecimal monthRate = BigDecimalUtils.getMonthRate(yearRate);
        BigDecimal monthRateAddOne = monthRate.add(BigDecimal.ONE);
        BigDecimal totalInterest = invest.multiply(monthRate);
        BigDecimal monthInterest;
        for (int i = 1; i < totalMonth + 1; i++) {
            BigDecimal sub = monthRateAddOne.pow(totalMonth).subtract(monthRateAddOne.pow(i - 1));
            monthInterest = totalInterest.multiply(sub)
                    .divide(monthRateAddOne.pow(totalMonth).subtract(BigDecimal.ONE), 2, RoundingMode.DOWN);
            map.put(i, monthInterest);
        }
        return map;
    }

    /**
     * 每月还款本金
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月偿还本金表
     */
    public static Map<Integer, BigDecimal> getPerMonthPrincipal(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        BigDecimal monthRate = BigDecimalUtils.getMonthRate(yearRate);
        BigDecimal monthRateAddOne = monthRate.add(BigDecimal.ONE);
        BigDecimal monthIncome = invest.multiply(monthRate).multiply(monthRateAddOne.pow(totalMonth))
                .divide(monthRateAddOne.pow(totalMonth).subtract(BigDecimal.ONE), 8, RoundingMode.DOWN);
        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, totalMonth);
        Map<Integer, BigDecimal> mapPrincipal = new HashMap<>(totalMonth);

        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            mapPrincipal.put(entry.getKey(), monthIncome.subtract(entry.getValue()));
        }
        return mapPrincipal;
    }

    /**
     * 每月本息
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月偿还本息
     */
    public static Map<Integer, BigDecimal> getPerMonthPrincipalInterest(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> map = new HashMap<>(totalMonth);
        BigDecimal monthRate = BigDecimalUtils.getMonthRate(yearRate);
        BigDecimal monthRateAddOne = monthRate.add(BigDecimal.ONE);
        BigDecimal monthIncome = invest.multiply(monthRate).multiply(monthRateAddOne.pow(totalMonth))
                .divide(monthRateAddOne.pow(totalMonth).subtract(BigDecimal.ONE), 8, RoundingMode.DOWN);
        for (int i = 1; i <= totalMonth; i++) {
            map.put(i, monthIncome);
        }
        return map;
    }

    /**
     * 总利息
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 总利息
     */
    public static BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        BigDecimal count = BigDecimal.ZERO;
        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, totalMonth);

        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            count = count.add(entry.getValue());
        }
        return count;
    }
}
