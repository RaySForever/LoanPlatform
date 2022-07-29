package xyz.raysmen.lp.core.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * UserBindVO
 * 账户绑定
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.vo
 * @date 2022/07/14 18:11
 */
@Getter
@Setter
@ToString
public class UserBindVO {
    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 银行类型
     */
    private String bankType;

    /**
     * 银行卡号
     */
    private String bankNo;

    /**
     * 手机号
     */
    private String mobile;
}
