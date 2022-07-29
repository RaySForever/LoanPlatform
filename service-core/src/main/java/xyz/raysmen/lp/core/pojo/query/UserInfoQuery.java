package xyz.raysmen.lp.core.pojo.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * UserInfoQuery
 * 用户信息搜索条件对象
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.query
 * @date 2022/06/09 21:13
 */
@Getter
@Setter
@ToString
public class UserInfoQuery {
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 1：出借人 2：借款人
     */
    private Integer userType;

    /**
     * 状态（0：锁定 1：正常）
     */
    private Integer status;
}
