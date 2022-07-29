package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.raysmen.lp.common.exception.CustomAssert;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.core.enums.TransTypeEnum;
import xyz.raysmen.lp.core.hfb.FormHelper;
import xyz.raysmen.lp.core.hfb.HfbConst;
import xyz.raysmen.lp.core.hfb.RequestHelper;
import xyz.raysmen.lp.core.mapper.UserAccountMapper;
import xyz.raysmen.lp.core.mapper.UserInfoMapper;
import xyz.raysmen.lp.core.pojo.bo.TransFlowBO;
import xyz.raysmen.lp.core.pojo.entity.UserAccount;
import xyz.raysmen.lp.core.pojo.entity.UserInfo;
import xyz.raysmen.lp.core.service.TransFlowService;
import xyz.raysmen.lp.core.service.UserAccountService;
import xyz.raysmen.lp.core.service.UserBindService;
import xyz.raysmen.lp.core.util.LendNoUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    private final UserInfoMapper userInfoMapper;

    private final TransFlowService transFlowService;

    private final UserBindService userBindService;

    @Autowired
    public UserAccountServiceImpl(UserInfoMapper userInfoMapper, TransFlowService transFlowService, UserBindService userBindService) {
        this.userInfoMapper = userInfoMapper;
        this.transFlowService = transFlowService;
        this.userBindService = userBindService;
    }

    /**
     * 充值并确认
     *
     * @param chargeAmt 充值金额
     * @param userId    用户ID
     * @return 返回充值自动提交表单字符串
     */
    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        // 获取用户信息
        UserInfo userInfo = userInfoMapper.selectById(userId);
        // 判断账户绑定状态
        String bindCode = userInfo.getBindCode();
        CustomAssert.notEmpty(bindCode, ResponseEnum.USER_NO_BIND_ERROR);

        // 创建参数表
        Map<String, Object> paramMap = getParamMap(bindCode, chargeAmt);

        // 构建充值自动提交表单
        return FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
    }

    /**
     * 充值回调响应
     *
     * @param paramMap 参数表
     * @return 回调响应，success
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String notify(Map<String, Object> paramMap) {
        // 客户充值订单号
        String agentBillNo = (String) paramMap.get("agentBillNo");
        // 判断交易流水是否存在
        if (transFlowService.isSaveTransFlow(agentBillNo)) {
            log.warn("幂等性返回");
            return "success";
        }
        log.info("充值成功");

        // 充值人绑定协议号
        String bindCode = (String) paramMap.get("bindCode");
        // 充值金额
        String chargeAmt = (String) paramMap.get("chargeAmt");
        // 账户处理更新
        this.baseMapper.updateAccount(bindCode, new BigDecimal(chargeAmt), BigDecimal.ZERO);

        // 记录账户流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,
                "充值");
        transFlowService.saveTransFlow(transFlowBO);

        return "success";
    }

    /**
     * 获取对应用户ID的账户余额
     *
     * @param userId 用户ID
     * @return 返回用户账户余额
     */
    @Override
    public BigDecimal getAccount(Long userId) {
        // 根据userId获取对应用户账户对象
        LambdaQueryWrapper<UserAccount> wrapper = new LambdaQueryWrapper<UserAccount>()
                .eq(UserAccount::getUserId, userId);
        UserAccount userAccount = this.baseMapper.selectOne(wrapper);
        // 返回账户中的余额
        return userAccount.getAmount();
    }

    /**
     * 通过构建的提现提交表单向汇付宝发起提现操作
     *
     * @param fetchAmt 提现金额
     * @param userId   用户ID
     * @return 返回构建的提现提交表单字符串
     */
    @Override
    public String commitWithdraw(BigDecimal fetchAmt, Long userId) {
        // 账户可用余额充足：当前用户的余额 >= 当前用户的提现金额
        // 获取当前用户的账户余额
        BigDecimal amount = getAccount(userId);
        CustomAssert.isTrue(amount.compareTo(fetchAmt) >= 0,
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);


        String bindCode = userBindService.getBindCodeByUserId(userId);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getWithdrawNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("fetchAmt", fetchAmt);
        paramMap.put("feeAmt", BigDecimal.ZERO);
        paramMap.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建自动提交表单
        return FormHelper.buildForm(HfbConst.WITHDRAW_URL, paramMap);
    }

    /**
     * 提现异步回调
     *
     * @param paramMap 请求参数表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyWithdraw(Map<String, Object> paramMap) {

        log.info("提现成功");
        String agentBillNo = (String) paramMap.get("agentBillNo");
        boolean result = transFlowService.isSaveTransFlow(agentBillNo);
        if (result) {
            log.warn("幂等性返回");
            return;
        }

        String bindCode = (String) paramMap.get("bindCode");
        String fetchAmt = (String) paramMap.get("fetchAmt");

        //根据用户账户修改账户金额
        baseMapper.updateAccount(bindCode, new BigDecimal("-" + fetchAmt), BigDecimal.ZERO);

        //增加交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(fetchAmt),
                TransTypeEnum.WITHDRAW,
                "提现");
        transFlowService.saveTransFlow(transFlowBO);
    }

    private Map<String, Object> getParamMap(String bindCode, BigDecimal chargeAmt) {
        Map<String, Object> paramMap = new HashMap<>(16);

        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", BigDecimal.ZERO);
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        return paramMap;
    }
}
