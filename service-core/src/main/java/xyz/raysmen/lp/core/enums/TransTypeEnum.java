package xyz.raysmen.lp.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TransTypeEnum
 * 交易流水类型枚举类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.enums
 * @date 2022/07/14 18:24
 */
@Getter
@AllArgsConstructor
public enum TransTypeEnum {
    /**
     * 类型：1,"充值"
     */
    RECHARGE(1,"充值"),
    /**
     * 类型：2,"投标锁定"
     */
    INVEST_LOCK(2,"投标锁定"),
    /**
     * 类型：3,"放款解锁"
     */
    INVEST_UNLOCK(3,"放款解锁"),
    /**
     * 类型：4,"撤标"
     */
    CANCEL_LEND(4,"撤标"),
    /**
     * 类型：5,"放款到账"
     */
    BORROW_BACK(5,"放款到账"),
    /**
     * 类型：6,"还款扣减"
     */
    RETURN_DOWN(6,"还款扣减"),
    /**
     * 类型：7,"出借回款"
     */
    INVEST_BACK(7,"出借回款"),
    /**
     * 类型：8,"提现"
     */
    WITHDRAW(8,"提现"),
    ;

    private final Integer transType ;
    private final String transTypeName;


    public static String getTransTypeName(int transType) {
        for (TransTypeEnum obj : TransTypeEnum.values()) {
            if (transType == obj.getTransType()) {
                return obj.getTransTypeName();
            }
        }
        return "";
    }

}
