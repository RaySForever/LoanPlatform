package xyz.raysmen.lp.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * UserBindEnum
 * 账户绑定枚举类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.enums
 * @date 2022/07/14 18:24
 */
@Getter
@AllArgsConstructor
public enum UserBindEnum {
    /**
     * 状态：0，未绑定
     */
    NO_BIND(0, "未绑定"),
    /**
     * 状态：1，绑定成功
     */
    BIND_OK(1, "绑定成功"),
    /**
     * 状态：-1，绑定失败
     */
    BIND_FAIL(-1, "绑定失败"),
    ;

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 信息
     */
    private final String msg;
}
