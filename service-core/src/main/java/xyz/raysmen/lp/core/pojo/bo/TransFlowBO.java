package xyz.raysmen.lp.core.pojo.bo;

import lombok.*;
import xyz.raysmen.lp.core.enums.TransTypeEnum;

import java.math.BigDecimal;

/**
 * TransFlowBO
 * 交易流水业务对象
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.bo
 * @date 2022/07/17 16:19
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransFlowBO {
    /**
     * 商户充值订单号
     */
    private String agentBillNo;
    /**
     * 绑定账户协议号
     */
    private String bindCode;
    /**
     * 充值金额
     */
    private BigDecimal amount;
    /**
     * 交易流水类型
     */
    private TransTypeEnum transTypeEnum;
    /**
     * 备忘
     */
    private String memo;
}
