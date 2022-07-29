package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
import xyz.raysmen.lp.core.mapper.LendItemMapper;
import xyz.raysmen.lp.core.mapper.LendMapper;
import xyz.raysmen.lp.core.mapper.UserAccountMapper;
import xyz.raysmen.lp.core.pojo.bo.TransFlowBO;
import xyz.raysmen.lp.core.pojo.entity.Lend;
import xyz.raysmen.lp.core.pojo.entity.LendItem;
import xyz.raysmen.lp.core.pojo.util.BeansConverter;
import xyz.raysmen.lp.core.pojo.vo.InvestVO;
import xyz.raysmen.lp.core.service.*;
import xyz.raysmen.lp.core.util.LendNoUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@Service
public class LendItemServiceImpl extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {

    private final LendMapper lendMapper;

    private final LendService lendService;

    private final UserAccountService userAccountService;

    private final UserBindService userBindService;

    private final TransFlowService transFlowService;

    private final UserAccountMapper userAccountMapper;

    @Autowired
    public LendItemServiceImpl(LendMapper lendMapper, LendService lendService, UserAccountService userAccountService, UserBindService userBindService, TransFlowService transFlowService, UserAccountMapper userAccountMapper) {
        this.lendMapper = lendMapper;
        this.lendService = lendService;
        this.userAccountService = userAccountService;
        this.userBindService = userBindService;
        this.transFlowService = transFlowService;
        this.userAccountMapper = userAccountMapper;
    }

    /**
     * 提交标的投资
     *
     * @param investVO 投标信息
     * @return 返回充值自动提交表单
     */
    @Override
    public String commitInvest(InvestVO investVO) {
        // 获取标的信息
        Long lendId = investVO.getLendId();
        Lend lend = lendMapper.selectById(lendId);

        // 标的状态必须为募资中
        CustomAssert.isTrue(
                LendStatusEnum.INVEST_RUN.getStatus().equals(lend.getStatus()),
                ResponseEnum.LEND_INVEST_ERROR);

        // 标的不能超卖：(已投金额 + 本次投资金额 ) > 标的金额（超卖）
        BigDecimal sum = lend.getInvestAmount().add(new BigDecimal(investVO.getInvestAmount()));
        CustomAssert.isTrue(sum.compareTo(lend.getAmount()) <= 0,
                ResponseEnum.LEND_FULL_SCALE_ERROR);

        // 获取当前用户的账户余额
        Long investUserId = investVO.getInvestUserId();
        BigDecimal amount = userAccountService.getAccount(investUserId);
        // 账户可用余额充足：当前用户的余额 >= 当前用户的投资金额（可以投资）
        CustomAssert.isTrue(amount.compareTo(new BigDecimal(investVO.getInvestAmount())) >= 0,
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        // 在商户平台中生成投资信息==========================================
        // 标的下的投资信息
        LendItem lendItem = new LendItem();
        BeansConverter.lendAndInvestVOToLendItem(lend, investVO, lendItem);
        // 投资人id
        lendItem.setInvestUserId(investUserId);
        // 投资条目编号（一个Lend对应一个或多个LendItem）
        String lendItemNo = LendNoUtils.getLendItemNo();
        lendItem.setLendItemNo(lendItemNo);
        // 投资时间
        lendItem.setInvestTime(LocalDateTime.now());

        // 预期收益
        BigDecimal expectAmount = lendService.getInterestCount(
                lendItem.getInvestAmount(),
                lendItem.getLendYearRate(),
                lend.getPeriod(),
                lend.getReturnMethod());
        lendItem.setExpectAmount(expectAmount);

        // 实际收益
        lendItem.setRealAmount(BigDecimal.ZERO);
        // 默认状态：刚刚创建
        lendItem.setStatus(0);
        // 记录投资信息
        baseMapper.insert(lendItem);


        // 组装投资相关的参数，提交到汇付宝资金托管平台==========================================
        // 在托管平台同步用户的投资信息，修改用户的账户资金信息===================================
        // 获取投资人的绑定协议号
        String bindCode = userBindService.getBindCodeByUserId(investUserId);
        // 获取借款人的绑定协议号
        String benefitBindCode = userBindService.getBindCodeByUserId(lend.getUserId());

        // 封装提交至汇付宝的参数
        Map<String, Object> paramMap = getParamMap(bindCode, benefitBindCode, lend, lendItemNo, investVO);

        // 构建投标自动提交表单
        return FormHelper.buildForm(HfbConst.INVEST_URL, paramMap);
    }

    /**
     * 会员投资异步回调
     *
     * @param paramMap  请求参数表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notify(Map<String, Object> paramMap) {
        log.info("投标成功");

        // 获取投资编号
        String agentBillNo = (String) paramMap.get("agentBillNo");

        // 检查投资编号是否已存在，保证接口幂等性
        boolean result = transFlowService.isSaveTransFlow(agentBillNo);
        if (result) {
            log.warn("幂等性返回");
            return;
        }

        // 获取用户的绑定协议号
        String bindCode = (String) paramMap.get("voteBindCode");
        String voteAmt = (String) paramMap.get("voteAmt");

        // 修改商户系统中的用户账户金额：将余额中的voteAmt钱数转移至冻结金额
        userAccountMapper.updateAccount(bindCode, new BigDecimal("-" + voteAmt), new BigDecimal(voteAmt));

        // 修改投资记录的投资状态改为已支付
        // 先获取投资记录
        LendItem lendItem = this.getByLendItemNo(agentBillNo);
        // 修改为已支付
        lendItem.setStatus(1);
        baseMapper.updateById(lendItem);

        // 修改标的信息：投资人数、已投金额
        Long lendId = lendItem.getLendId();
        Lend lend = lendMapper.selectById(lendId);
        lend.setInvestNum(lend.getInvestNum() + 1);
        lend.setInvestAmount(lend.getInvestAmount().add(lendItem.getInvestAmount()));
        lendMapper.updateById(lend);

        // 新增交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(voteAmt),
                TransTypeEnum.INVEST_LOCK,
                "投资项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle());
        transFlowService.saveTransFlow(transFlowBO);
    }

    /**
     * 获取投资列表信息
     *
     * @param lendId 标的编号
     * @param status 状态（0：默认 1：已支付 2：已还款）
     * @return 返回投资列表信息
     */
    @Override
    public List<LendItem> selectByLendId(Long lendId, Integer status) {
        LambdaQueryWrapper<LendItem> wrapper = new LambdaQueryWrapper<LendItem>()
                .eq(LendItem::getLendId, lendId)
                .eq(LendItem::getStatus, status);
        return this.baseMapper.selectList(wrapper);
    }

    /**
     * 获取投资列表信息
     *
     * @param lendId 标的编号
     * @return 返回投资列表信息
     */
    @Override
    public List<LendItem> selectByLendId(Long lendId) {
        LambdaQueryWrapper<LendItem> wrapper = new LambdaQueryWrapper<LendItem>()
                .eq(LendItem::getLendId, lendId);
        return this.baseMapper.selectList(wrapper);
    }

    private Map<String, Object> getParamMap(String bindCode, String benefitBindCode, Lend lend, String lendItemNo, InvestVO investVO) {
        Map<String, Object> paramMap = new HashMap<>();

        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("voteBindCode", bindCode);
        paramMap.put("benefitBindCode", benefitBindCode);
        // 项目标号
        paramMap.put("agentProjectCode", lend.getLendNo());
        paramMap.put("agentProjectName", lend.getTitle());

        // 在资金托管平台上的投资订单的唯一编号，要和lendItemNo保持一致。
        // 订单编号
        paramMap.put("agentBillNo", lendItemNo);
        paramMap.put("voteAmt", investVO.getInvestAmount());
        paramMap.put("votePrizeAmt", "0");
        paramMap.put("voteFeeAmt", "0");
        // 标的总金额
        paramMap.put("projectAmt", lend.getAmount());
        paramMap.put("note", "");
        // 检查常量是否正确
        paramMap.put("notifyUrl", HfbConst.INVEST_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.INVEST_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        return paramMap;
    }

    private LendItem getByLendItemNo(String lendItemNo) {
        LambdaQueryWrapper<LendItem> queryWrapper = new LambdaQueryWrapper<LendItem>()
                .eq(LendItem::getLendItemNo, lendItemNo);
        return baseMapper.selectOne(queryWrapper);
    }
}
