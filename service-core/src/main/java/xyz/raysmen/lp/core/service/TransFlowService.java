package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.bo.TransFlowBO;
import xyz.raysmen.lp.core.pojo.entity.TransFlow;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface TransFlowService extends IService<TransFlow> {
    /**
     * 保存交易流水业务
     *
     * @param transFlowBO   交易流水业务
     */
    void saveTransFlow(TransFlowBO transFlowBO);

    /**
     * 根据客户充值订单号是否已存在来判断流水存在与否
     *
     * @param agentBillNo   客户充值订单号
     * @return              返回客户充值订单号是否已存在
     */
    boolean isSaveTransFlow(String agentBillNo);

    /**
     * 通过用户ID获取交易流水业务列表
     *
     * @param userId    用户ID
     * @return          返回交易流水业务列表
     */
    List<TransFlow> selectByUserId(Long userId);
}
