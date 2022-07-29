package xyz.raysmen.lp.core.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * BorrowerApprovalVO
 * 借款人审批
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.vo
 * @date 2022/07/16 19:37
 */
@Getter
@Setter
@ToString
public class BorrowerApprovalVO {
    /**
     * 借款人ID
     */
    private Long borrowerId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 身份证信息是否正确
     */
    private Boolean isIdCardOk;

    /**
     * 房产信息是否正确
     */
    private Boolean isHouseOk;

    /**
     * 车辆信息是否正确
     */
    private Boolean isCarOk;

    /**
     * 基本信息积分
     */
    private Integer infoIntegral;
}
