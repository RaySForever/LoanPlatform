package xyz.raysmen.lp.core.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * UserIndexVO
 * 首页用户信息
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.vo
 * @date 2022/07/19 02:07
 */
@Getter
@Setter
@ToString
public class UserIndexVO {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 1：出借人 2：借款人
     */
    private Integer userType;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 绑定状态（0：未绑定，1：绑定成功 -1：绑定失败）
     */
    private Integer bindStatus;

    /**
     * 帐户可用余额
     */
    private BigDecimal amount;

    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;

    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;
}
