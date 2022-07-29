package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.raysmen.lp.common.exception.CustomAssert;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.core.enums.LendStatusEnum;
import xyz.raysmen.lp.core.enums.TransTypeEnum;
import xyz.raysmen.lp.core.hfb.FormHelper;
import xyz.raysmen.lp.core.hfb.HfbConst;
import xyz.raysmen.lp.core.hfb.RequestHelper;
import xyz.raysmen.lp.core.mapper.*;
import xyz.raysmen.lp.core.pojo.bo.TransFlowBO;
import xyz.raysmen.lp.core.pojo.entity.Lend;
import xyz.raysmen.lp.core.pojo.entity.LendItem;
import xyz.raysmen.lp.core.pojo.entity.LendItemReturn;
import xyz.raysmen.lp.core.pojo.entity.LendReturn;
import xyz.raysmen.lp.core.service.LendReturnService;
import xyz.raysmen.lp.core.service.TransFlowService;
import xyz.raysmen.lp.core.service.UserAccountService;
import xyz.raysmen.lp.core.service.UserBindService;
import xyz.raysmen.lp.core.util.LendNoUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@Service
public class LendReturnServiceImpl extends ServiceImpl<LendReturnMapper, LendReturn> implements LendReturnService {

    private final UserAccountService userAccountService;

    private final LendMapper lendMapper;

    private final UserBindService userBindService;

    private final ObjectMapper objectMapper;

    private final LendItemMapper lendItemMapper;

    private final TransFlowService transFlowService;

    private final UserAccountMapper userAccountMapper;

    private final LendItemReturnMapper lendItemReturnMapper;

    @Autowired
    public LendReturnServiceImpl(UserAccountService userAccountService, LendMapper lendMapper,
                                 UserBindService userBindService, ObjectMapper objectMapper,
                                 LendItemMapper lendItemMapper, TransFlowService transFlowService,
                                 UserAccountMapper userAccountMapper, LendItemReturnMapper lendItemReturnMapper) {
        this.userAccountService = userAccountService;
        this.lendMapper = lendMapper;
        this.userBindService = userBindService;
        this.objectMapper = objectMapper;
        this.lendItemMapper = lendItemMapper;
        this.transFlowService = transFlowService;
        this.userAccountMapper = userAccountMapper;
        this.lendItemReturnMapper = lendItemReturnMapper;
    }

    /**
     * 获取还款记录列表信息
     *
     * @param lendId 标的编号
     * @return 返回还款记录列表信息
     */
    @Override
    public List<LendReturn> selectByLendId(Long lendId) {
        LambdaQueryWrapper<LendReturn> wrapper = new LambdaQueryWrapper<LendReturn>()
                .eq(LendReturn::getLendId, lendId);
        return this.baseMapper.selectList(wrapper);
    }

    /**
     * 用户还款
     *
     * @param lendReturnId 还款计划ID
     * @param userId       用户ID
     * @return 返回构建的还款提交表单字符串
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String commitReturn(Long lendReturnId, Long userId) {
        // 获取还款记录
        LendReturn lendReturn = baseMapper.selectById(lendReturnId);

        // 判断账号余额是否充足
        BigDecimal amount = userAccountService.getAccount(userId);
        CustomAssert.isTrue(amount.compareTo(lendReturn.getTotal()) >= 0,
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        // 获取借款人绑定编码
        String bindCode = userBindService.getBindCodeByUserId(userId);
        // 获取标的对象
        Long lendId = lendReturn.getLendId();
        Lend lend = lendMapper.selectById(lendId);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        // 商户商品名称
        paramMap.put("agentGoodsName", lend.getTitle());
        // 批次号
        paramMap.put("agentBatchNo", lendReturn.getReturnNo());
        // 还款人绑定协议号
        paramMap.put("fromBindCode", bindCode);
        // 还款总额
        paramMap.put("totalAmt", lendReturn.getTotal());
        paramMap.put("note", "");
        // 还款明细
        List<Map<String, Object>> lendItemReturnDetailList = addReturnDetail(lendReturnId);
        String lendItemReturnDetails = null;
        try {
            lendItemReturnDetails = objectMapper.writeValueAsString(lendItemReturnDetailList);
        } catch (JsonProcessingException e) {
            log.error("Jackson序列化异常：JsonProcessingException：{}", ExceptionUtils.getStackTrace(e));
        }
        paramMap.put("data", lendItemReturnDetails);
        paramMap.put("voteFeeAmt", new BigDecimal(0));
        paramMap.put("notifyUrl", HfbConst.BORROW_RETURN_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.BORROW_RETURN_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建自动提交表单
        return FormHelper.buildForm(HfbConst.BORROW_RETURN_URL, paramMap);
    }

    /**
     * 添加还款计划明细
     *
     * @param lendReturnId 还款计划ID
     * @return 返回还款计划明细列表
     */
    @Override
    public List<Map<String, Object>> addReturnDetail(Long lendReturnId) {
        // 获取还款记录
        LendReturn lendReturn = this.baseMapper.selectById(lendReturnId);
        // 获取标的信息
        Lend lend = lendMapper.selectById(lendReturn.getLendId());

        // 根据还款ID获取回款列表
        List<LendItemReturn> lendItemReturnList = this.selectLendItemReturnList(lendReturnId);
        List<Map<String, Object>> lendItemReturnDetailList = new ArrayList<>();
        for (LendItemReturn lendItemReturn : lendItemReturnList) {
            LendItem lendItem = lendItemMapper.selectById(lendItemReturn.getLendItemId());
            String bindCode = userBindService.getBindCodeByUserId(lendItem.getInvestUserId());

            Map<String, Object> map = new HashMap<>();
            // 项目编号
            map.put("agentProjectCode", lend.getLendNo());
            // 出借编号
            map.put("voteBillNo", lendItem.getLendItemNo());
            // 收款人（出借人）
            map.put("toBindCode", bindCode);
            // 还款金额
            map.put("transitAmt", lendItemReturn.getTotal());
            // 还款本金
            map.put("baseAmt", lendItemReturn.getPrincipal());
            // 还款利息
            map.put("benifitAmt", lendItemReturn.getInterest());
            // 商户手续费
            map.put("feeAmt", BigDecimal.ZERO);

            lendItemReturnDetailList.add(map);
        }
        return lendItemReturnDetailList;
    }

    /**
     * 获取还款计划列表
     *
     * @param lendReturnId 还款计划ID
     * @return 返回还款计划列表
     */
    @Override
    public List<LendItemReturn> selectLendItemReturnList(Long lendReturnId) {
        LambdaQueryWrapper<LendItemReturn> wrapper = new LambdaQueryWrapper<LendItemReturn>()
                .eq(LendItemReturn::getLendReturnId, lendReturnId);
        return lendItemReturnMapper.selectList(wrapper);
    }

    /**
     * 还款异步回调
     *
     * @param paramMap 请求参数表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notify(Map<String, Object> paramMap) {
        log.info("还款成功");

        // 还款编号
        String agentBatchNo = (String) paramMap.get("agentBatchNo");

        boolean result = transFlowService.isSaveTransFlow(agentBatchNo);
        if (result) {
            log.warn("幂等性返回");
            return;
        }

        // 获取还款数据
        String voteFeeAmt = (String) paramMap.get("voteFeeAmt");
        LambdaQueryWrapper<LendReturn> wrapper = new LambdaQueryWrapper<LendReturn>()
                .eq(LendReturn::getReturnNo, agentBatchNo);
        LendReturn lendReturn = this.baseMapper.selectOne(wrapper);

        // 更新还款状态
        lendReturn.setStatus(1);
        lendReturn.setFee(new BigDecimal(voteFeeAmt));
        lendReturn.setRealReturnTime(LocalDateTime.now());
        baseMapper.updateById(lendReturn);

        // 获取标的信息
        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        // 最后一次还款更新标的状态
        if (lendReturn.getLast()) {
            lend.setStatus(LendStatusEnum.PAY_OK.getStatus());
            lendMapper.updateById(lend);
        }

        // 借款账号转出金额
        // 获取还款金额
        BigDecimal totalAmt = new BigDecimal((String) paramMap.get("totalAmt"));
        String bindCode = userBindService.getBindCodeByUserId(lend.getUserId());
        userAccountMapper.updateAccount(bindCode, totalAmt.negate(), BigDecimal.ZERO);

        //借款人交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBatchNo,
                bindCode,
                totalAmt,
                TransTypeEnum.RETURN_DOWN,
                "借款人还款扣减，项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle());
        transFlowService.saveTransFlow(transFlowBO);

        // 获取回款明细
        List<LendItemReturn> lendItemReturnList = selectLendItemReturnList(lendReturn.getId());
        lendItemReturnList.forEach(item -> {
            // 更新回款状态
            item.setStatus(1);
            item.setRealReturnTime(LocalDateTime.now());
            lendItemReturnMapper.updateById(item);

            // 更新出借信息
            LendItem lendItem = lendItemMapper.selectById(item.getLendItemId());
            lendItem.setRealAmount(item.getInterest());
            lendItemMapper.updateById(lendItem);

            // 投资账号转入金额
            String investBindCode = userBindService.getBindCodeByUserId(item.getInvestUserId());
            userAccountMapper.updateAccount(investBindCode, item.getTotal(), BigDecimal.ZERO);

            // 投资账号交易流水
            TransFlowBO investTransFlowBO = new TransFlowBO(
                    LendNoUtils.getReturnItemNo(),
                    investBindCode,
                    item.getTotal(),
                    TransTypeEnum.INVEST_BACK,
                    "还款到账，项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle());
            transFlowService.saveTransFlow(investTransFlowBO);

        });
    }
}
