package xyz.raysmen.lp.core.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * LoginVO
 * 登录信息-视图层对象
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.vo
 * @date 2022/06/08 21:39
 */
@Getter
@Setter
@ToString
public class LoginVO {
    /**
     * 1：出借人 2：借款人
     */
    private Integer userType;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;
}
