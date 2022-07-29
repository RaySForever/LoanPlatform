package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.BorrowInfo;
import xyz.raysmen.lp.core.pojo.entity.Lend;
import xyz.raysmen.lp.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface LendService extends IService<Lend> {
    /**
     * 创建标的
     *
     * @param borrowInfoApprovalVO  借款审批信息
     * @param borrowInfo            借款信息
     */
    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    /**
     * 获取标的列表
     *
     * @return  返回标的列表
     */
    List<Lend> selectList();

    /**
     * 获取对应ID的标的信息详情表
     *
     * @param id    标的ID
     * @return      标的信息详情表
     */
    Map<String, Object> getLendDetail(Long id);

    /**
     * 计算投资收益
     *
     * @param invest        投资金额
     * @param yearRate      年化收益
     * @param totalMonth    期数
     * @param returnMethod  还款方式
     * @return              返回投资收益
     */
    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalMonth, Integer returnMethod);

    /**
     * 满标放款
     *
     * @param lendId    标的id
     */
    void makeLoan(Long lendId);
}
