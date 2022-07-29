package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.UserAccount;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface UserAccountService extends IService<UserAccount> {
    /**
     * 充值并确认
     *
     * @param chargeAmt 充值金额
     * @param userId    用户ID
     * @return          返回充值自动提交表单字符串
     */
    String commitCharge(BigDecimal chargeAmt, Long userId);

    /**
     * 回调响应
     *
     * @param paramMap  参数表
     * @return          回调响应，success
     */
    String notify(Map<String, Object> paramMap);

    /**
     * 获取对应用户ID的账户余额
     *
     * @param userId    用户ID
     * @return          返回用户账户余额
     */
    BigDecimal getAccount(Long userId);

    /**
     * 通过构建的提现提交表单向汇付宝发起提现操作
     *
     * @param fetchAmt  提现金额
     * @param userId    用户ID
     * @return          返回构建的提现提交表单字符串
     */
    String commitWithdraw(BigDecimal fetchAmt, Long userId);

    /**
     * 提现异步回调
     *
     * @param paramMap 请求参数表
     */
    void notifyWithdraw(Map<String, Object> paramMap);
}
