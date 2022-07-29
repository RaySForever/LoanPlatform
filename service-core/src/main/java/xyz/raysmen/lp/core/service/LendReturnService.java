package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.LendItemReturn;
import xyz.raysmen.lp.core.pojo.entity.LendReturn;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface LendReturnService extends IService<LendReturn> {
    /**
     * 获取还款记录列表信息
     *
     * @param lendId    标的编号
     * @return          返回还款记录列表信息
     */
    List<LendReturn> selectByLendId(Long lendId);

    /**
     * 用户还款
     *
     * @param lendReturnId  还款计划ID
     * @param userId        用户ID
     * @return              返回构建的还款提交表单字符串
     */
    String commitReturn(Long lendReturnId, Long userId);

    /**
     * 添加还款计划明细
     *
     * @param lendReturnId  还款计划ID
     * @return              返回还款计划明细列表
     */
    List<Map<String, Object>> addReturnDetail(Long lendReturnId);

    /**
     * 获取还款计划列表
     *
     * @param lendReturnId  还款计划ID
     * @return              返回还款计划列表
     */
    List<LendItemReturn> selectLendItemReturnList(Long lendReturnId);

    /**
     * 还款异步回调
     *
     * @param paramMap  请求参数表
     */
    void notify(Map<String, Object> paramMap);
}
