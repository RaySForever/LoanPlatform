package xyz.raysmen.lp.core.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * InvestVO
 * 投标信息
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.vo
 * @date 2022/07/17 22:28
 */
@Getter
@Setter
@ToString
public class InvestVO {
    /**
     * 标的编号
     */
    private Long lendId;

    /**
     * 投标金额
     */
    private String investAmount;

    /**
     * 投资用户id
     */
    private Long investUserId;

    /**
     * 投资用户姓名
     */
    private String investName;
}
