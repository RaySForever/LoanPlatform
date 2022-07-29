package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.raysmen.lp.common.exception.CustomAssert;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.core.util.BigDecimalUtils;
import xyz.raysmen.lp.core.enums.BorrowInfoStatusEnum;
import xyz.raysmen.lp.core.enums.BorrowerStatusEnum;
import xyz.raysmen.lp.core.enums.UserBindEnum;
import xyz.raysmen.lp.core.mapper.BorrowInfoMapper;
import xyz.raysmen.lp.core.mapper.BorrowerMapper;
import xyz.raysmen.lp.core.mapper.IntegralGradeMapper;
import xyz.raysmen.lp.core.mapper.UserInfoMapper;
import xyz.raysmen.lp.core.pojo.entity.BorrowInfo;
import xyz.raysmen.lp.core.pojo.entity.Borrower;
import xyz.raysmen.lp.core.pojo.entity.IntegralGrade;
import xyz.raysmen.lp.core.pojo.entity.UserInfo;
import xyz.raysmen.lp.core.pojo.vo.BorrowInfoApprovalVO;
import xyz.raysmen.lp.core.pojo.vo.BorrowerDetailVO;
import xyz.raysmen.lp.core.service.BorrowInfoService;
import xyz.raysmen.lp.core.service.BorrowerService;
import xyz.raysmen.lp.core.service.DictService;
import xyz.raysmen.lp.core.service.LendService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {
    private final UserInfoMapper userInfoMapper;

    private final IntegralGradeMapper integralGradeMapper;

    private final DictService dictService;

    private final BorrowerMapper borrowerMapper;

    private final BorrowerService borrowerService;

    private final LendService lendService;
    @Autowired
    public BorrowInfoServiceImpl(UserInfoMapper userInfoMapper, IntegralGradeMapper integralGradeMapper, DictService dictService, BorrowerMapper borrowerMapper, BorrowerService borrowerService, LendService lendService) {
        this.userInfoMapper = userInfoMapper;
        this.integralGradeMapper = integralGradeMapper;
        this.dictService = dictService;
        this.borrowerMapper = borrowerMapper;
        this.borrowerService = borrowerService;
        this.lendService = lendService;
    }

    /**
     * 通过用户ID获取借款额度
     *
     * @param userId 用户ID
     * @return 返回该用户的借款额度
     */
    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        // 获取用户积分
        UserInfo userInfo = userInfoMapper.selectById(userId);
        CustomAssert.isNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        Integer integral = userInfo.getIntegral();

        // 根据积分查询借款额度
        LambdaQueryWrapper<IntegralGrade> wrapper = new LambdaQueryWrapper<IntegralGrade>()
                .le(IntegralGrade::getIntegralStart, integral)
                .ge(IntegralGrade::getIntegralEnd, integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(wrapper);
        // 未查出则返回 0
        return integralGrade == null ? BigDecimal.ZERO : integralGrade.getBorrowAmount();
    }

    /**
     * 保存当前借款申请信息
     *
     * @param borrowInfo 借款申请信息
     * @param userId     用户ID
     */
    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        // 获取userInfo的用户数据
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 判断用户绑定状态
        CustomAssert.isTrue(
                UserBindEnum.BIND_OK.getStatus().equals(userInfo.getBindStatus()),
                ResponseEnum.USER_NO_BIND_ERROR);

        // 判断用户信息是否审批通过
        CustomAssert.isTrue(
                BorrowerStatusEnum.AUTH_OK.getStatus().equals(userInfo.getBorrowAuthStatus()),
                ResponseEnum.USER_NO_AMOUNT_ERROR);

        // 判断借款额度是否足够
        BigDecimal borrowAmount = getBorrowAmount(userId);
        CustomAssert.isTrue(
                borrowAmount.compareTo(borrowInfo.getAmount()) >= 0,
                ResponseEnum.USER_AMOUNT_LESS_ERROR);

        // 百分比转成小数，默认除以100，保留小数点后 2 位
        BigDecimal borrowYearRate = BigDecimalUtils.percentToDecimal(borrowInfo.getBorrowYearRate());
        borrowInfo.setBorrowYearRate(borrowYearRate);
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        borrowInfo.setUserId(userId);
        // 插入数据
        this.baseMapper.insert(borrowInfo);
    }

    /**
     * 通过用户ID获取借款申请审批状态
     *
     * @param userId 用户ID
     * @return 返回借款申请审批状态
     */
    @Override
    public Integer getStatusByUserId(Long userId) {
        LambdaQueryWrapper<BorrowInfo> wrapper = new LambdaQueryWrapper<BorrowInfo>()
                .select(BorrowInfo::getStatus)
                .eq(BorrowInfo::getUserId, userId);
        List<Object> objects = this.baseMapper.selectObjs(wrapper);

        // 查出空集合即借款人尚未提交信息
        return objects.size() == 0 ? BorrowInfoStatusEnum.NO_AUTH.getStatus() : (Integer) objects.get(0);
    }

    /**
     * 获取借款信息列表
     *
     * @return 返回借款信息列表
     */
    @Override
    public List<BorrowInfo> selectList() {
        List<BorrowInfo> borrowInfoList = this.baseMapper.selectBorrowInfoList();
        borrowInfoList.forEach(this::setParamToBorrowInfo);
        return borrowInfoList;
    }

    /**
     * 通过借款id获取借款信息详情表
     *
     * @param id 借款id
     * @return 返回借款信息详情表
     */
    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {
        // 查询借款对象
        BorrowInfo borrowInfo = this.baseMapper.selectById(id);
        // 组装数据
        setParamToBorrowInfo(borrowInfo);

        // 根据user_id获取借款人对象
        LambdaQueryWrapper<Borrower> wrapper = new LambdaQueryWrapper<Borrower>()
                .eq(Borrower::getUserId, borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(wrapper);
        // 组装借款人对象
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());

        // 组装数据
        Map<String, Object> result = new HashMap<>();
        result.put("borrowInfo", borrowInfo);
        result.put("borrower", borrowerDetailVO);
        return result;
    }

    /**
     * 审批借款信息
     *
     * @param borrowInfoApprovalVO 借款审批信息
     */
    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        // 修改借款信息状态
        Long borrowInfoId = borrowInfoApprovalVO.getId();
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoId);
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        baseMapper.updateById(borrowInfo);

        // 审核通过则创建标的
        if (BorrowInfoStatusEnum.CHECK_OK.getStatus().equals(borrowInfoApprovalVO.getStatus())) {
            //创建标的
            lendService.createLend(borrowInfoApprovalVO, borrowInfo);
        }
    }

    private void setParamToBorrowInfo(BorrowInfo borrowInfo) {
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowInfo.getParam().put("returnMethod", returnMethod);
        borrowInfo.getParam().put("moneyUse", moneyUse);
        borrowInfo.getParam().put("status", status);
    }
}
