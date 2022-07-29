package xyz.raysmen.lp.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * ResponseEnum
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.common.result
 * @date 2022/05/18 21:17
 * @description 响应结果枚举类
 */
@Getter
@AllArgsConstructor
@ToString
public enum ResponseEnum {
    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    ERROR(-1, "服务器内部错误"),

    /**
     * -1xx 服务器错误
     */
    BAD_SQL_GRAMMAR_ERROR(-101, "sql语法错误"),
    /**
     * 详见-2xx 参数校验
     */
    SERVLET_ERROR(-102, "servlet请求异常"),
    UPLOAD_ERROR(-103, "文件上传错误"),
    EXPORT_DATA_ERROR(-104, "数据导出失败"),


    /**
     * -2xx 参数校验
     */
    BORROW_AMOUNT_NULL_ERROR(-201, "借款额度不能为空"),
    MOBILE_NULL_ERROR(-202, "手机号码不能为空"),
    MOBILE_ERROR(-203, "手机号码不正确"),
    EMAIL_NULL_ERROR(-204, "电子邮箱不能为空"),
    EMAIL_ERROR(-205, "电子邮箱地址不正确"),
    PASSWORD_NULL_ERROR(-206, "密码不能为空"),
    CODE_NULL_ERROR(-207, "验证码不能为空"),
    CODE_ERROR(-208, "验证码错误"),
    MOBILE_EXIST_ERROR(-209, "手机号已被注册"),
    LOGIN_MOBILE_ERROR(-210, "用户不存在"),
    LOGIN_PASSWORD_ERROR(-211, "密码错误"),
    LOGIN_LOKED_ERROR(-212, "用户被锁定"),
    LOGIN_AUTH_ERROR(-213, "未登录"),


    USER_BIND_IDCARD_EXIST_ERROR(-301, "身份证号码已绑定"),
    USER_NO_BIND_ERROR(-302, "用户未绑定"),
    USER_NO_AMOUNT_ERROR(-303, "用户信息未审核"),
    USER_AMOUNT_LESS_ERROR(-304, "您的借款额度不足"),
    LEND_INVEST_ERROR(-305, "当前状态无法投标"),
    LEND_FULL_SCALE_ERROR(-306, "已满标，无法投标"),
    NOT_SUFFICIENT_FUNDS_ERROR(-307, "余额不足，请充值"),

    PAY_UNIFIEDORDER_ERROR(-401, "统一下单错误"),

    /**
     * 业务限流
     */
    TENCENTCLOUD_SMS_LIMIT_CONTROL_ERROR(-502, "短信发送过于频繁"),
    /**
     * 其他失败
     */
    TENCENTCLOUD_SMS_ERROR(-503, "短信发送失败"),
    TENCENTCLOUD_TEMP_KEY_ERROR(-504, "永久秘钥无效"),
    TENCENTCLOUD_COS_SERVICE_ERROR(-505, "COS服务无法处理"),
    TENCENTCLOUD_COS_CLIENT_ERROR(-506, "无法从服务获得有效响应"),

    WEIXIN_CALLBACK_PARAM_ERROR(-601, "回调参数不正确"),
    WEIXIN_FETCH_ACCESSTOKEN_ERROR(-602, "获取access_token失败"),
    WEIXIN_FETCH_USERINFO_ERROR(-603, "获取用户信息失败"),;

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 消息
     */
    private final String message;
}
