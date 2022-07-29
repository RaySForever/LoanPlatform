package xyz.raysmen.lp.core.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * UserInfoVO
 * 用户信息-视图层对象
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.vo
 * @date 2022/06/08 21:42
 */
@Getter
@Setter
@ToString
public class UserInfoVO {
    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 1：出借人 2：借款人
     */
    private Integer userType;

    /**
     * 邮箱
     */
    private String email;

    /**
     * JWT访问令牌
     */
    private String token;
}
