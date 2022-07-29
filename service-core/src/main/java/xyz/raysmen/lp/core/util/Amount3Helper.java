package xyz.raysmen.lp.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 按月付息到期还本工具类
 *
 * @author Rays
 * @date 2022-07-17
 */
public class Amount3Helper {

    /**
     * 每月还款利息
     * 按月付息，到期还本-计算获取还款方式为按月付息，到期还本的每月偿还利息
     * 公式：每月应还利息=总借款额*年利率÷还款月数
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月偿还利息
     */
    public static Map<Integer, BigDecimal> getPerMonthInterest(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> map = new HashMap<>(totalMonth);
        //每月偿还利息
        BigDecimal monthInterest = invest.multiply(BigDecimalUtils.getMonthRate(yearRate));
        for (int i = 1; i <= totalMonth; i++) {
            map.put(i, monthInterest);
        }
        return map;
    }

    /**
     * 每月偿还本金，按月付息到期还本
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月偿还本金，实际到期还本
     */
    public static Map<Integer, BigDecimal> getPerMonthPrincipal(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> map = new HashMap<>(totalMonth);
        // 每月偿还本金
        for (int i = 1; i <= totalMonth; i++) {
            // 实际到期还本
            if (i == totalMonth) {
                map.put(i, invest);
            } else {
                map.put(i, BigDecimal.ZERO);
            }
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
        //每月偿还利息
        BigDecimal count = invest.multiply(BigDecimalUtils.getMonthRate(yearRate));

        return count.multiply(new BigDecimal(totalMonth)).setScale(2, RoundingMode.DOWN);
    }
}