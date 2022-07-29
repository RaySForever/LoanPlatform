package xyz.raysmen.lp.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * CustomResult
 * 统一返回结果类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.common.result
 * @date 2022/05/18 21:53
 * @description 统一返回结果类
 */
@Data
public class CustomResult {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回数据
     */
    private Map<String, Object> data = new HashMap<>();

    /**
     * 构造器私有
     */
    private CustomResult(){}

    /**
     * 返回成功
     */
    public static CustomResult ok(){
        CustomResult r = new CustomResult();
        r.setCode(ResponseEnum.SUCCESS.getCode());
        r.setMessage(ResponseEnum.SUCCESS.getMessage());
        return r;
    }

    /**
     * 返回失败
     */
    public static CustomResult error(){
        CustomResult r = new CustomResult();
        r.setCode(ResponseEnum.ERROR.getCode());
        r.setMessage(ResponseEnum.ERROR.getMessage());
        return r;
    }

    /**
     * 设置特定结果
     */
    public static CustomResult setResult(ResponseEnum responseEnum){
        CustomResult r = new CustomResult();
        r.setCode(responseEnum.getCode());
        r.setMessage(responseEnum.getMessage());
        return r;
    }

    /**
     * 设置特定的响应消息
     *
     * @param message 响应消息
     * @return 返回结果本身
     */
    public CustomResult message(String message){
        this.setMessage(message);
        return this;
    }

    /**
     * 设置特定的响应码
     *
     * @param code 响应码
     * @return 返回结果本身
     */
    public CustomResult code(Integer code){
        this.setCode(code);
        return this;
    }

    public CustomResult data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public CustomResult data(Map<String, Object> map){
        this.setData(map);
        return this;
    }
}
