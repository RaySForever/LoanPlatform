package xyz.raysmen.lp.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BorrowInfoStatusEnum
 * 借款申请提交状态枚举类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.enums
 * @date 2022/07/15 18:24
 */
@Getter
@AllArgsConstructor
public enum BorrowInfoStatusEnum {

    /**
     * 状态：0, "未认证"
     */
    NO_AUTH(0, "未认证"),
    /**
     * 状态：1, "审核中"
     */
    CHECK_RUN(1, "审核中"),
    /**
     * 状态：2, "审核通过"
     */
    CHECK_OK(2, "审核通过"),
    /**
     * 状态：-1, "审核不通过"
     */
    CHECK_FAIL(-1, "审核不通过"),
    ;

    private final Integer status;
    private final String msg;

    public static String getMsgByStatus(int status) {
        for (BorrowInfoStatusEnum obj : BorrowInfoStatusEnum.values()) {
            if (status == obj.getStatus()) {
                return obj.getMsg();
            }
        }
        return "";
    }
}
