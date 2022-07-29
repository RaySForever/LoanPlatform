package xyz.raysmen.lp.common.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import xyz.raysmen.lp.common.result.ResponseEnum;

/**
 * BusinessException
 * 自定义业务异常类，使用一个或较少的异常类，可以捕获和显示所有的异常信息。
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.common.exception
 * @date 2022/05/19 11:24
 * @description 自定义业务异常类，使用一个或较少的异常类，可以捕获和显示所有的异常信息。
 */
@Getter
@Setter
@ToString
public class BusinessException extends RuntimeException {
    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误消息
     */
    private String message;

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public BusinessException() {
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     * @param message 错误消息
     */
    public BusinessException(String message) {
        this.message = message;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause   原始异常对象
     * @param code    错误码
     * @param message 错误消息
     * @since 1.4
     */
    public BusinessException(Throwable cause, Integer code, String message) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    /**
     * @param responseEnum 接收枚举类型
     */
    public BusinessException(ResponseEnum responseEnum) {
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }

    /**
     * @param cause        原始异常对象
     * @param responseEnum 接收枚举类型
     */
    public BusinessException(Throwable cause, ResponseEnum responseEnum) {
        super(cause);
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }
}
