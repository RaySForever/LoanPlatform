package xyz.raysmen.lp.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import xyz.raysmen.lp.common.result.ResponseEnum;

import java.util.Objects;

/**
 * CustomAssert
 * 自定义断言，模仿Spring优雅的 Assert(断言) 方式来校验业务的异常情况，消除 if else
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.common.exception
 * @date 2022/05/19 11:57
 */
@Slf4j
public class CustomAssert {
    /**
     * 断言对象不为空
     * obj 为空则抛异常
     *
     * @param obj        断言对象
     * @param responseEnum  具体响应情况
     */
    public static void notNull(Object obj, ResponseEnum responseEnum){
        if(obj == null){
            log.info("断言的对象为null.....................");
            throw new BusinessException(responseEnum);
        }
    }


    /**
     * 断言对象为空
     * 如果对象obj不为空，则抛出异常
     *
     * @param obj           断言对象
     * @param responseEnum  具体响应情况
     */
    public static void isNull(Object obj, ResponseEnum responseEnum) {
        if (obj != null) {
            log.info("断言的对象不为null......");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言表达式为真
     * 如果不为真，则抛出异常
     *
     * @param expression 是否成功
     */
    public static void isTrue(boolean expression, ResponseEnum responseEnum) {
        if (!expression) {
            log.info("断言表达式为false...............");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言两个对象不相等
     * 如果相等，则抛出异常
     *
     * @param o1            断言对象
     * @param o2            断言对象
     * @param responseEnum  具体响应情况
     */
    public static void notEquals(Object o1, Object o2,  ResponseEnum responseEnum) {
        if (Objects.equals(o1, o2)) {
            log.info("断言的两个对象相等...............");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言两个对象相等
     * 如果不相等，则抛出异常
     *
     * @param o1            断言对象
     * @param o2            断言对象
     * @param responseEnum  具体响应情况
     */
    public static void equals(Object o1, Object o2,  ResponseEnum responseEnum) {
        if (!Objects.equals(o1, o2)) {
            log.info("断言两个对象不相等...............");
            throw new BusinessException(responseEnum);
        }
    }

    /**
     * 断言字符串类型参数不为空或空字符串
     * 如果为空，则抛出异常
     *
     * @param s             断言的字符串类型参数
     * @param responseEnum  具体响应情况
     */
    public static void notEmpty(String s, ResponseEnum responseEnum) {
        if (!StringUtils.hasLength(s)) {
            log.info("断言的字符串类型参数为空...............");
            throw new BusinessException(responseEnum);
        }
    }
}
