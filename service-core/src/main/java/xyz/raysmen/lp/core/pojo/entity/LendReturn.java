package xyz.raysmen.lp.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 还款记录表
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Getter
@Setter
@TableName("lend_return")
public class LendReturn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标的id
     */
    private Long lendId;

    /**
     * 借款信息id
     */
    private Long borrowInfoId;

    /**
     * 还款批次号
     */
    private String returnNo;

    /**
     * 借款人用户id
     */
    private Long userId;

    /**
     * 借款金额
     */
    private BigDecimal amount;

    /**
     * 计息本金额
     */
    private BigDecimal baseAmount;

    /**
     * 当前的期数
     */
    private Integer currentPeriod;

    /**
     * 年化利率
     */
    private BigDecimal lendYearRate;

    /**
     * 还款方式 1-等额本息 2-等额本金 3-每月还息一次还本 4-一次还本
     */
    private Integer returnMethod;

    /**
     * 本金
     */
    private BigDecimal principal;

    /**
     * 利息
     */
    private BigDecimal interest;

    /**
     * 本息
     */
    private BigDecimal total;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 还款时指定的还款日期
     */
    private LocalDate returnDate;

    /**
     * 实际发生的还款时间
     */
    private LocalDateTime realReturnTime;

    /**
     * 是否逾期
     */
    @TableField("is_overdue")
    private Boolean overdue;

    /**
     * 逾期金额
     */
    private BigDecimal overdueTotal;

    /**
     * 是否最后一次还款
     */
    @TableField("is_last")
    private Boolean last;

    /**
     * 状态（0-未归还 1-已归还）
     */
    @TableField("`status`")
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除(1:已删除，0:未删除)
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
