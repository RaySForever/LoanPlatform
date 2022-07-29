package xyz.raysmen.lp.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BorrowerStatusEnum
 * 借款人认证状态枚举类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.enums
 * @date 2022/07/15 18:24
 */
@Getter
@AllArgsConstructor
public enum BorrowerStatusEnum {

    /**
     * 状态：0, "未认证"
     */
    NO_AUTH(0, "未认证"),
    /**
     * 状态：1, "认证中"
     */
    AUTH_RUN(1, "认证中"),
    /**
     * 状态：2, "认证成功"
     */
    AUTH_OK(2, "认证成功"),
    /**
     * 状态：-1, "认证失败"
     */
    AUTH_FAIL(-1, "认证失败"),
    ;

    private final Integer status;
    private final String msg;

    public static String getMsgByStatus(int status) {
        for (BorrowerStatusEnum obj : BorrowerStatusEnum.values()) {
            if (status == obj.getStatus()) {
                return obj.getMsg();
            }
        }
        return "";
    }
}

