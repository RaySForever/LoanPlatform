package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.LendItem;
import xyz.raysmen.lp.core.pojo.vo.InvestVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface LendItemService extends IService<LendItem> {
    /**
     * 提交标的投资
     *
     * @param investVO  投标信息
     * @return          返回充值自动提交表单
     */
    String commitInvest(InvestVO investVO);

    /**
     * 会员投资异步回调
     *
     * @param paramMap  请求参数表
     */
    void notify(Map<String, Object> paramMap);

    /**
     * 获取投资列表信息
     *
     * @param lendId    标的编号
     * @param status    状态（0：默认 1：已支付 2：已还款）
     * @return          返回投资列表信息
     */
    List<LendItem> selectByLendId(Long lendId, Integer status);

    /**
     * 获取投资列表信息
     *
     * @param lendId    标的编号
     * @return          返回投资列表信息
     */
    List<LendItem> selectByLendId(Long lendId);
}
