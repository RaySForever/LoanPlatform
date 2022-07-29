package xyz.raysmen.lp.core.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import xyz.raysmen.lp.core.pojo.entity.BorrowerAttach;

import java.util.List;

/**
 * BorrowerVO
 * 借款人认证信息
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.vo
 * @date 2022/07/15 18:28
 */
@Getter
@Setter
@ToString
public class BorrowerVO {
    /**
     * 性别（1：男 0：女）
     */
    private Integer sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 学历
     */
    private Integer education;

    /**
     * 是否结婚（1：是 0：否）
     */
    private Boolean marry;

    /**
     * 行业
     */
    private Integer industry;

    /**
     * 月收入
     */
    private Integer income;

    /**
     * 还款来源
     */
    private Integer returnSource;

    /**
     * 联系人名称
     */
    private String contactsName;

    /**
     * 联系人手机
     */
    private String contactsMobile;

    /**
     * 联系人关系
     */
    private Integer contactsRelation;

    /**
     * 借款人附件资料
     */
    private List<BorrowerAttach> borrowerAttachList;
}
