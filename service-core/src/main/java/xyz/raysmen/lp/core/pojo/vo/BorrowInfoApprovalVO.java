package xyz.raysmen.lp.core.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * BorrowInfoApprovalVO
 * 借款信息审批
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.vo
 * @date 2022/07/17 11:10
 */
@Getter
@Setter
@ToString
public class BorrowInfoApprovalVO {
    /**
     * ID
     */
    private Long id;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 审批内容
     */
    private String content;

    /**
     * 标题
     */
    private String title;

    /**
     * 年化利率
     */
    private BigDecimal lendYearRate;

    /**
     * 平台服务费率
     */
    private BigDecimal serviceRate;

    /**
     * 开始日期
     */
    private String lendStartDate;

    /**
     * 描述信息
     */
    private String lendInfo;
}
