package xyz.raysmen.lp.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.common.result.ResponseEnum;

/**
 * UnifiedExceptionHandler
 *
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.common.exception
 * @date 2022/05/19 10:12
 * @description 统一异常处理器
 */
@Slf4j
@RestControllerAdvice
public class UnifiedExceptionHandler {
    /**
     * 捕获自定义异常，BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public CustomResult handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return CustomResult.error().message(e.getMessage()).code(e.getCode());
    }

    /**
     * 捕获特定异常，BadSqlGrammarException
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public CustomResult handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error(e.getMessage(), e);
        return CustomResult.setResult(ResponseEnum.BAD_SQL_GRAMMAR_ERROR);
    }

    /**
     * Controller上一层相关异常，即进入Controller前的可能会出现的异常
     */
    @ExceptionHandler({
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodArgumentNotValidException.class,
            HttpMediaTypeNotAcceptableException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
            AsyncRequestTimeoutException.class
    })
    public CustomResult handleServletException(Exception e) {
        log.error(e.getMessage(), e);
        // 对前端展示简易信息：SERVLET_ERROR(-102, "servlet请求异常")
        return CustomResult.setResult(ResponseEnum.SERVLET_ERROR);
    }

    /**
     * 未定义异常
     * 当controller中抛出Exception，则捕获
     */
    @ExceptionHandler(value = Exception.class)
    public CustomResult handleException(Exception e) {
        // 输出至日志来查看
        log.error(e.getMessage(), e);
        // 返回前端则展示简易信息
        return CustomResult.error();
    }
}
