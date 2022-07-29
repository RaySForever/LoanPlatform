package xyz.raysmen.lp.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;


/**
 * 等额本金工具类
 * 校验网址：http://www.xjumc.com/
 * 等额本金是指一种贷款的还款方式，是在还款期内把贷款数总额等分，每月偿还同等数额的本金和剩余贷款在该月所产生的利息，这样由于每月的还款本金额固定，
 * 而利息越来越少，借款人起初还款压力较大，但是随时间的推移每月还款数也越来越少。
 *
 * @author Rays
 * @date 2022-07-17
 */
public class Amount2Helper {

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
        // 每月本金
        BigDecimal monthPrincipal = invest.divide(new BigDecimal(totalMonth), 8, RoundingMode.DOWN);
        // 获取月利率
        BigDecimal monthRate = BigDecimalUtils.getMonthRate(yearRate);
        for (int i = 1; i <= totalMonth; i++) {
            BigDecimal monthInterest = invest.subtract(monthPrincipal.multiply(new BigDecimal(i - 1))).multiply(monthRate);
            BigDecimal monthPrincipalInterest = monthPrincipal.add(monthInterest).setScale(2, RoundingMode.DOWN);
            map.put(i, monthPrincipalInterest);
        }
        return map;
    }

    /**
     * 每月还款利息
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月偿还利息表
     */
    public static Map<Integer, BigDecimal> getPerMonthInterest(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> inMap = new HashMap<>(totalMonth);
        // 每月本金
        BigDecimal monthPrincipal = invest.divide(new BigDecimal(totalMonth), 8, RoundingMode.DOWN);
        // 获取月利率
        BigDecimal monthRate = BigDecimalUtils.getMonthRate(yearRate);
        for (int i = 1; i <= totalMonth; i++) {
            BigDecimal repayment = monthPrincipal.multiply(new BigDecimal(i - 1));
            BigDecimal monthInterest = invest.subtract(repayment).multiply(monthRate).setScale(2, RoundingMode.DOWN);
            inMap.put(i, monthInterest);
        }
        return inMap;
    }

    /**
     * 每月还款本金
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月还款本金表
     */
    public static Map<Integer, BigDecimal> getPerMonthPrincipal(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> map = new HashMap<>();
        BigDecimal monthIncome = invest.divide(new BigDecimal(totalMonth), 8, RoundingMode.DOWN);
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
     * @return 返回总利息
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