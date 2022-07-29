package xyz.raysmen.lp.core.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BorrowerDetailVO
 * 借款人信息详情
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.vo
 * @date 2022/07/16 14:39
 */
@Getter
@Setter
@ToString
public class BorrowerDetailVO {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 性别（1：男 0：女）
     */
    private String sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 学历
     */
    private String education;

    /**
     * 是否结婚（1：是 0：否）
     */
    private String marry;

    /**
     * 行业
     */
    private String industry;

    /**
     * 月收入
     */
    private String income;

    /**
     * 还款来源
     */
    private String returnSource;

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
    private String contactsRelation;

    /**
     * 审核状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 借款人附件资料
     */
    private List<BorrowerAttachVO> borrowerAttachVOList;
}
