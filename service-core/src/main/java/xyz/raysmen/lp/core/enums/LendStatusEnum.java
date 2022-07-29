package xyz.raysmen.lp.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * LendStatusEnum
 * 标的状态枚举类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.enums
 * @date 2022/07/14 18:24
 */
@Getter
@AllArgsConstructor
public enum LendStatusEnum {

    /**
     * 状态：0, "待发布"
     */
    CHECK(0, "待发布"),
    /**
     * 状态：1, "募资中"
     */
    INVEST_RUN(1, "募资中"),
    /**
     * 状态：2, "还款中"
     */
    PAY_RUN(2, "还款中"),
    /**
     * 状态：3, "已结清"
     */
    PAY_OK(3, "已结清"),
    /**
     * 状态：4, "结标"
     */
    FINISH(4, "结标"),
    /**
     * 状态：-1, "已撤标"
     */
    CANCEL(-1, "已撤标"),
//    OVERDUE(-1, "逾期催收中"),
//    BAD_BILL(-2, "坏账"),
    ;

    private final Integer status;
    private final String msg;


    public static String getMsgByStatus(int status) {
        for (LendStatusEnum obj : LendStatusEnum.values()) {
            if (status == obj.getStatus()) {
                return obj.getMsg();
            }
        }
        return "";
    }
}
