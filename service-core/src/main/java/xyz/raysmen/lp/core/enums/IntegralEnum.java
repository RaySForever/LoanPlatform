package xyz.raysmen.lp.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * IntegralEnum
 * 积分度量枚举类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.enums
 * @date 2022/07/15 18:24
 */
@Getter
@AllArgsConstructor
public enum IntegralEnum {

//    BORROWER_INFO(50, "借款人基本信息"),
    /**
     * 完善借款人身份证信息，增加积分30
     */
    BORROWER_IDCARD(30, "借款人身份证信息"),
    /**
     * 完善借款人房产信息，增加积分100
     */
    BORROWER_HOUSE(100, "借款人房产信息"),
    /**
     * 完善借款人车辆信息，增加积分60
     */
    BORROWER_CAR(60, "借款人车辆信息"),
    ;

    private final Integer integral;
    private final String msg;
}
