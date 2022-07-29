package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.raysmen.lp.common.exception.BusinessException;
import xyz.raysmen.lp.common.exception.CustomAssert;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.core.enums.LendStatusEnum;
import xyz.raysmen.lp.core.enums.ReturnMethodEnum;
import xyz.raysmen.lp.core.enums.TransTypeEnum;
import xyz.raysmen.lp.core.hfb.HfbConst;
import xyz.raysmen.lp.core.hfb.RequestHelper;
import xyz.raysmen.lp.core.mapper.BorrowerMapper;
import xyz.raysmen.lp.core.mapper.LendMapper;
import xyz.raysmen.lp.core.mapper.UserAccountMapper;
import xyz.raysmen.lp.core.mapper.UserInfoMapper;
import xyz.raysmen.lp.core.pojo.bo.TransFlowBO;
import xyz.raysmen.lp.core.pojo.entity.*;
import xyz.raysmen.lp.core.pojo.util.BeansConverter;
import xyz.raysmen.lp.core.pojo.vo.BorrowInfoApprovalVO;
import xyz.raysmen.lp.core.pojo.vo.BorrowerDetailVO;
import xyz.raysmen.lp.core.service.*;
import xyz.raysmen.lp.core.util.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@Service
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    private final DictService dictService;

    private final BorrowerMapper borrowerMapper;

    private final BorrowerService borrowerService;

    private final UserInfoMapper userInfoMapper;

    private final UserAccountMapper userAccountMapper;

    private final LendItemService lendItemService;

    private final TransFlowService transFlowService;

    private final ObjectMapper objectMapper;

    private final LendReturnService lendReturnService;

    private final LendItemReturnService lendItemReturnService;

    @Autowired
    public LendServiceImpl(DictService dictService, BorrowerMapper borrowerMapper,
                           BorrowerService borrowerService, UserInfoMapper userInfoMapper,
                           UserAccountMapper userAccountMapper,@Lazy LendItemService lendItemService,
                           TransFlowService transFlowService, ObjectMapper objectMapper,
                           LendReturnService lendReturnService, LendItemReturnService lendItemReturnService) {
        this.dictService = dictService;
        this.borrowerMapper = borrowerMapper;
        this.borrowerService = borrowerService;
        this.userInfoMapper = userInfoMapper;
        this.userAccountMapper = userAccountMapper;
        this.lendItemService = lendItemService;
        this.transFlowService = transFlowService;
        this.objectMapper = objectMapper;
        this.lendReturnService = lendReturnService;
        this.lendItemReturnService = lendItemReturnService;
    }

    /**
     * 创建标的
     *
     * @param borrowInfoApprovalVO 借款审批信息
     * @param borrowInfo           借款信息
     */
    @Override
    public void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {
        Lend lend = new Lend();
        BeansConverter.borrowInfoApprovalVOAndBorrowInfoToLend(borrowInfoApprovalVO, borrowInfo, lend);
        // 生成编号
        lend.setLendNo(LendNoUtils.getLendNo());

        lend.setLowestAmount(BigDecimalUtils.ONE_HUNDRED);
        lend.setInvestAmount(BigDecimal.ZERO);
        lend.setInvestNum(0);
        lend.setPublishDate(LocalDateTime.now());

        // 实际收益
        lend.setRealAmount(BigDecimal.ZERO);
        // 状态
        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());
        // 审核时间
        lend.setCheckTime(LocalDateTime.now());
        // 审核人（应为当前审核人 ID，现在暂定为 1L）
        lend.setCheckAdminId(1L);

        this.baseMapper.insert(lend);
    }

    /**
     * 获取标的列表
     *
     * @return 返回标的列表
     */
    @Override
    public List<Lend> selectList() {
        List<Lend> lendList = this.baseMapper.selectList(null);

        lendList.forEach(this::setParamToLend);
        return lendList;
    }

    /**
     * 获取对应ID的标的信息详情表
     *
     * @param id 标的ID
     * @return 标的信息详情表
     */
    @Override
    public Map<String, Object> getLendDetail(Long id) {
        // 查询标的对象
        Lend lend = baseMapper.selectById(id);

        // 组装数据
        setParamToLend(lend);

        // 根据user_id获取借款人对象
        LambdaQueryWrapper<Borrower> wrapper = new LambdaQueryWrapper<Borrower>().eq(Borrower::getUserId, lend.getUserId());
        Borrower borrower = borrowerMapper.selectOne(wrapper);
        // 组装借款人详细信息对象
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());

        // 组装数据
        Map<String, Object> lendDetails = new HashMap<>();
        lendDetails.put("lend", lend);
        lendDetails.put("borrower", borrowerDetailVO);
        return lendDetails;
    }

    /**
     * 计算投资收益
     *
     * @param invest       投资金额
     * @param yearRate     年化收益
     * @param totalMonth   期数
     * @param returnMethod 还款方式
     * @return 返回投资收益
     */
    @Override
    public BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalMonth, Integer returnMethod) {
        // 依据对应的还款方式计算总利息
        BigDecimal interestCount;
        if (ReturnMethodEnum.ONE.getMethod().equals(returnMethod)) {
            interestCount = Amount1Helper.getInterestCount(invest, yearRate, totalMonth);
        } else if (ReturnMethodEnum.TWO.getMethod().equals(returnMethod)) {
            interestCount = Amount2Helper.getInterestCount(invest, yearRate, totalMonth);
        } else if (ReturnMethodEnum.THREE.getMethod().equals(returnMethod)) {
            interestCount = Amount3Helper.getInterestCount(invest, yearRate, totalMonth);
        } else {
            interestCount = Amount4Helper.getInterestCount(invest, yearRate, totalMonth);
        }
        return interestCount;
    }

    /**
     * 满标放款
     *
     * @param lendId 标的id
     */
    @Override
    public void makeLoan(Long lendId) {
        // 获取标的信息
        Lend lend = baseMapper.selectById(lendId);

        // 平台收益，放款扣除，借款人借款实际金额=借款金额-平台收益
        // 获取月年化
        BigDecimal monthRate = BigDecimalUtils.getMonthRate(lend.getServiceRate());
        // 平台实际收益 = 已投金额 * 月年化 * 标的期数
        BigDecimal realAmount = BigDecimalUtils.getExpectAmount(lend.getInvestAmount(),
                monthRate, new BigDecimal(lend.getPeriod()));

        // 放款编号
        String agentBillNo = LendNoUtils.getLoanNo();

        // 放款接口调用参数表
        Map<String, Object> paramMap = getParamMap(lend, agentBillNo, realAmount);

        log.info("放款参数：" + paramMap);
        // 发送同步远程调用
        String result = RequestHelper.sendRequest(paramMap, HfbConst.MAKE_LOAD_URL);
        JsonNode node;
        try {
            node = objectMapper.readTree(result);
        } catch (JsonProcessingException e) {
            throw new BusinessException(e, ResponseEnum.ERROR);
        }
        CustomAssert.notNull(node, ResponseEnum.ERROR);

        log.info("放款结果（字符串）：" + result);
        log.info("放款结果（JsonNode）：" + node);

        // 放款失败
        JsonNode resultCode = node.get(HfbConst.RESULT_CODE);
        CustomAssert.notNull(resultCode, ResponseEnum.ERROR);
        if (!HfbConst.MAKE_LOAD_SUCCESS_CODE.equals(resultCode.asText())) {
            JsonNode resultMsg = node.get(HfbConst.RESULT_MSG);
            CustomAssert.notNull(resultMsg, ResponseEnum.ERROR);
            throw new BusinessException(resultMsg.asText());
        }

        // 更新标的信息
        lend.setRealAmount(realAmount);
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setPaymentTime(LocalDateTime.now());
        baseMapper.updateById(lend);

        // 获取借款人信息
        Long userId = lend.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();

        // 给借款账号转入金额
        JsonNode voteAmt = node.get("voteAmt");
        CustomAssert.notNull(voteAmt, ResponseEnum.ERROR);
        BigDecimal total = new BigDecimal(voteAmt.asText());
        userAccountMapper.updateAccount(bindCode, total, BigDecimal.ZERO);

        // 新增借款人交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                total,
                TransTypeEnum.BORROW_BACK,
                // 项目编号
                "借款放款到账，编号：" + lend.getLendNo());
        transFlowService.saveTransFlow(transFlowBO);

        // 获取投资列表信息
        List<LendItem> lendItemList = lendItemService.selectByLendId(lendId, 1);
        lendItemList.forEach(item -> {

            // 获取投资人信息
            Long investUserId = item.getInvestUserId();
            UserInfo investUserInfo = userInfoMapper.selectById(investUserId);
            String investBindCode = investUserInfo.getBindCode();

            // 投资人账号冻结金额转出
            // 获取投资金额
            BigDecimal investAmount = item.getInvestAmount();
            userAccountMapper.updateAccount(investBindCode, BigDecimal.ZERO, investAmount.negate());

            // 新增投资人交易流水
            TransFlowBO investTransFlowBO = new TransFlowBO(
                    LendNoUtils.getTransNo(),
                    investBindCode,
                    investAmount,
                    TransTypeEnum.INVEST_UNLOCK,
                    "冻结资金转出，出借放款，编号：" + lend.getLendNo());//项目编号
            transFlowService.saveTransFlow(investTransFlowBO);
        });

        //放款成功生成借款人还款计划和投资人回款计划
        this.repaymentPlan(lend);
    }

    private void setParamToLend(Lend lend) {
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod());
        String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
        lend.getParam().put("returnMethod", returnMethod);
        lend.getParam().put("status", status);
    }

    private Map<String, Object> getParamMap(Lend lend, String agentBillNo, BigDecimal realAmount) {
        Map<String, Object> paramMap = new HashMap<>();

        paramMap.put("agentId", HfbConst.AGENT_ID);
        // 标的编号
        paramMap.put("agentProjectCode", lend.getLendNo());
        // 放款编号
        paramMap.put("agentBillNo", agentBillNo);

        // 商户手续费(平台实际收益)
        paramMap.put("mchFee", realAmount);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        return paramMap;
    }

    /**
     * 基于标的生成还款计划，附带回款计划
     *
     * @param lend  具体的标的对象
     */
    private void repaymentPlan(Lend lend) {
        // 还款计划列表
        List<LendReturn> lendReturnList = new ArrayList<>();

        // 按还款时间生成还款计划
        int period = lend.getPeriod();
        for (int i = 1; i <= period; i++) {

            // 创建还款计划对象
            LendReturn lendReturn = new LendReturn();
            lendReturn.setReturnNo(LendNoUtils.getReturnNo());
            lendReturn.setLendId(lend.getId());
            lendReturn.setBorrowInfoId(lend.getBorrowInfoId());
            lendReturn.setUserId(lend.getUserId());
            lendReturn.setAmount(lend.getAmount());
            lendReturn.setBaseAmount(lend.getInvestAmount());
            lendReturn.setLendYearRate(lend.getLendYearRate());
            // 当前期数
            lendReturn.setCurrentPeriod(i);
            lendReturn.setReturnMethod(lend.getReturnMethod());

            // 说明：还款计划中的这三项 = 回款计划中对应的这三项和
            // 因此需要先生成对应的回款计划：Principal、Interest、Total

            lendReturn.setFee(BigDecimal.ZERO);
            // 第二个月开始还款
            lendReturn.setReturnDate(lend.getLendStartDate().plusMonths(i));
            lendReturn.setOverdue(false);
            // 当为最后一个月时，标识为最后一次还款（TRUE），否则为FALSE
            lendReturn.setLast(i == period);
            lendReturn.setStatus(0);
            lendReturnList.add(lendReturn);
        }
        // 批量保存
        lendReturnService.saveBatch(lendReturnList);

        // 获取lendReturnList中还款期数与还款计划id映射map
        Map<Integer, Long> lendReturnMap = lendReturnList.stream().collect(
                Collectors.toMap(LendReturn::getCurrentPeriod, LendReturn::getId)
        );

        //======================================================
        //=============获取所有投资者，生成回款计划===================
        //======================================================
        // 回款计划列表
        List<LendItemReturn> lendItemReturnAllList = new ArrayList<>();
        // 获取投资成功的投资记录
        List<LendItem> lendItemList = lendItemService.selectByLendId(lend.getId(), 1);
        for (LendItem lendItem : lendItemList) {

            // 创建回款计划列表
            List<LendItemReturn> lendItemReturnList = this.returnInvest(lendItem.getId(), lendReturnMap, lend);
            lendItemReturnAllList.addAll(lendItemReturnList);
        }

        // 更新还款计划中的相关金额数据
        for (LendReturn lendReturn : lendReturnList) {

            BigDecimal sumPrincipal = lendItemReturnAllList.stream()
                    // 过滤条件：当回款计划中的还款计划id == 当前还款计划id的时候
                    .filter(item -> item.getLendReturnId().longValue() == lendReturn.getId().longValue())
                    // 将所有回款计划中计算的每月应收本金相加
                    .map(LendItemReturn::getPrincipal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal sumInterest = lendItemReturnAllList.stream()
                    .filter(item -> item.getLendReturnId().longValue() == lendReturn.getId().longValue())
                    .map(LendItemReturn::getInterest)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal sumTotal = lendItemReturnAllList.stream()
                    .filter(item -> item.getLendReturnId().longValue() == lendReturn.getId().longValue())
                    .map(LendItemReturn::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 每期还款本金
            lendReturn.setPrincipal(sumPrincipal);
            // 每期还款利息
            lendReturn.setInterest(sumInterest);
            // 每期还款本息
            lendReturn.setTotal(sumTotal);
        }
        lendReturnService.updateBatchById(lendReturnList);
    }

    /**
     * 回款计划
     *
     * @param lendItemId    投资记录编号
     * @param lendReturnMap 还款期数与还款计划id的映射map
     * @param lend          具体标的
     * @return              返回回款计划列表
     */
    public List<LendItemReturn> returnInvest(Long lendItemId, Map<Integer, Long> lendReturnMap, Lend lend) {
        // 投标信息
        LendItem lendItem = lendItemService.getById(lendItemId);

        // 投资金额
        BigDecimal amount = lendItem.getInvestAmount();
        // 年化利率
        BigDecimal yearRate = lendItem.getLendYearRate();
        // 投资期数
        int totalMonth = lend.getPeriod();

        // 还款期数 -> 利息
        Map<Integer, BigDecimal> mapInterest;
        // 还款期数 -> 本金
        Map<Integer, BigDecimal> mapPrincipal;

        // 根据还款方式计算本金和利息
        Integer returnMethod = lend.getReturnMethod();
        if (ReturnMethodEnum.ONE.getMethod().equals(returnMethod)) {
            // 利息
            mapInterest = Amount1Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            // 本金
            mapPrincipal = Amount1Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else if (ReturnMethodEnum.TWO.getMethod().equals(returnMethod)) {
            mapInterest = Amount2Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            mapPrincipal = Amount2Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else if (ReturnMethodEnum.THREE.getMethod().equals(returnMethod)) {
            mapInterest = Amount3Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            mapPrincipal = Amount3Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else {
            mapInterest = Amount4Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            mapPrincipal = Amount4Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        }

        // 创建回款计划列表
        List<LendItemReturn> lendItemReturnList = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            Integer currentPeriod = entry.getKey();
            // 根据还款期数获取还款计划的id
            Long lendReturnId = lendReturnMap.get(currentPeriod);

            LendItemReturn lendItemReturn = new LendItemReturn();
            lendItemReturn.setLendReturnId(lendReturnId);
            lendItemReturn.setLendItemId(lendItemId);
            lendItemReturn.setInvestUserId(lendItem.getInvestUserId());
            lendItemReturn.setLendId(lendItem.getLendId());
            lendItemReturn.setInvestAmount(lendItem.getInvestAmount());
            lendItemReturn.setLendYearRate(lend.getLendYearRate());
            lendItemReturn.setCurrentPeriod(currentPeriod);
            lendItemReturn.setReturnMethod(returnMethod);
            // 最后一次本金计算
            if (lendItemReturnList.size() > 0 && currentPeriod == totalMonth) {
                // 最后一期本金 = 本金 - 前几次之和
                BigDecimal sumPrincipal = lendItemReturnList.stream()
                        .map(LendItemReturn::getPrincipal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                // 最后一期应还本金 = 用当前投资人的总投资金额 - 除了最后一期前面期数计算出来的所有的应还本金
                BigDecimal lastPrincipal = lendItem.getInvestAmount().subtract(sumPrincipal);
                lendItemReturn.setPrincipal(lastPrincipal);
            } else {
                lendItemReturn.setPrincipal(mapPrincipal.get(currentPeriod));
                lendItemReturn.setInterest(mapInterest.get(currentPeriod));
            }

            lendItemReturn.setTotal(lendItemReturn.getPrincipal().add(lendItemReturn.getInterest()));
            lendItemReturn.setFee(BigDecimal.ZERO);
            lendItemReturn.setReturnDate(lend.getLendStartDate().plusMonths(currentPeriod));
            // 是否逾期，因为新建，默认未逾期
            lendItemReturn.setOverdue(false);
            lendItemReturn.setStatus(0);

            lendItemReturnList.add(lendItemReturn);
        }
        lendItemReturnService.saveBatch(lendItemReturnList);

        return lendItemReturnList;
    }
}
