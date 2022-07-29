package xyz.raysmen.lp.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ReturnMethodEnum
 * 还款方式枚举类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.enums
 * @date 2022/07/14 18:24
 */
@Getter
@AllArgsConstructor
public enum ReturnMethodEnum {

    /**
     * 还款方式：1, "等额本息"
     */
    ONE(1, "等额本息"),
    /**
     * 还款方式：2, "等额本金"
     */
    TWO(2, "等额本金"),
    /**
     * 还款方式：3, "每月还息一次还本"
     */
    THREE(3, "每月还息一次还本"),
    /**
     * 还款方式：4, "一次还本还息"
     */
    FOUR(4, "一次还本还息"),
    ;

    private final Integer method;
    private final String msg;
}
