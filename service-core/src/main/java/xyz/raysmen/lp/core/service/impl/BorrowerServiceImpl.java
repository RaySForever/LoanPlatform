package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xyz.raysmen.lp.core.enums.BorrowerStatusEnum;
import xyz.raysmen.lp.core.enums.IntegralEnum;
import xyz.raysmen.lp.core.mapper.BorrowerAttachMapper;
import xyz.raysmen.lp.core.mapper.BorrowerMapper;
import xyz.raysmen.lp.core.mapper.UserInfoMapper;
import xyz.raysmen.lp.core.mapper.UserIntegralMapper;
import xyz.raysmen.lp.core.pojo.entity.Borrower;
import xyz.raysmen.lp.core.pojo.entity.BorrowerAttach;
import xyz.raysmen.lp.core.pojo.entity.UserInfo;
import xyz.raysmen.lp.core.pojo.entity.UserIntegral;
import xyz.raysmen.lp.core.pojo.util.BeansConverter;
import xyz.raysmen.lp.core.pojo.vo.BorrowerApprovalVO;
import xyz.raysmen.lp.core.pojo.vo.BorrowerAttachVO;
import xyz.raysmen.lp.core.pojo.vo.BorrowerDetailVO;
import xyz.raysmen.lp.core.pojo.vo.BorrowerVO;
import xyz.raysmen.lp.core.service.BorrowerAttachService;
import xyz.raysmen.lp.core.service.BorrowerService;
import xyz.raysmen.lp.core.service.DictService;

import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    private final BorrowerAttachMapper borrowerAttachMapper;

    private final UserInfoMapper userInfoMapper;

    private final DictService dictService;

    private final BorrowerAttachService borrowerAttachService;

    private final UserIntegralMapper userIntegralMapper;

    @Autowired
    public BorrowerServiceImpl(BorrowerAttachMapper borrowerAttachMapper, UserInfoMapper userInfoMapper, DictService dictService, BorrowerAttachService borrowerAttachService, UserIntegralMapper userIntegralMapper) {
        this.borrowerAttachMapper = borrowerAttachMapper;
        this.userInfoMapper = userInfoMapper;
        this.dictService = dictService;
        this.borrowerAttachService = borrowerAttachService;
        this.userIntegralMapper = userIntegralMapper;
    }

    /**
     * 保存借款人信息
     *
     * @param borrowerVO 借款人认证信息
     * @param userId     用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 保存借款人信息
        Borrower borrower = BeansConverter.borrowerVOToBorrowerForSave(borrowerVO, userInfo, userId);
        this.baseMapper.insert(borrower);

        // 保存附件
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });

        // 更新会员状态，更新为认证中
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 通过用户ID获取借款人认证状态
     *
     * @param userId 用户ID
     * @return 返回借款人认证状态，数字形式
     */
    @Override
    public Integer getStatusByUserId(Long userId) {
        // 获取借款人认证状态
        LambdaQueryWrapper<Borrower> wrapper = new LambdaQueryWrapper<Borrower>()
                .select(Borrower::getStatus)
                .eq(Borrower::getUserId, userId);
        List<Object> objects = this.baseMapper.selectObjs(wrapper);

        // 检查借款人是否未提交信息
        if (objects.size() == 0) {
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        // 已提交信息则返回当前状态
        return (Integer) objects.get(0);
    }

    /**
     * 获取借款人分页列表
     *
     * @param pageParam 传入带有分页参数的分页对象
     * @param keyword   查询关键词
     * @return 返回带有查询结果的分页对象
     */
    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {
        if (!StringUtils.hasLength(keyword)) {
            return this.baseMapper.selectPage(pageParam, null);
        }
        LambdaQueryWrapper<Borrower> wrapper = new LambdaQueryWrapper<Borrower>()
                .like(Borrower::getName, keyword)
                .or().like(Borrower::getIdCard, keyword)
                .or().like(Borrower::getMobile, keyword)
                .orderByDesc(Borrower::getId);
        return this.baseMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 通过 id 获取借款人信息
     *
     * @param id 借款人id
     * @return 返回借款人信息详情
     */
    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {
        // 获取借款人信息
        Borrower borrower = this.baseMapper.selectById(id);

        // 填充基本借款人信息，还包括婚否、性别和审批状态
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        BeansConverter.borrowerToBorrowerDetailVO(borrower, borrowerDetailVO);

        // 计算下拉列表选中内容
        String education = dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation());
        String industry = dictService.getNameByParentDictCodeAndValue("moneyUse", borrower.getIndustry());
        String income = dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource());
        String contactsRelation = dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation());

        // 设置下拉列表选中内容
        borrowerDetailVO.setEducation(education);
        borrowerDetailVO.setIndustry(industry);
        borrowerDetailVO.setIncome(income);
        borrowerDetailVO.setReturnSource(returnSource);
        borrowerDetailVO.setContactsRelation(contactsRelation);

        // 获取附件VO列表
        List<BorrowerAttachVO> borrowerAttachVOList = borrowerAttachService.selectBorrowerAttachVOList(id);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);

        return borrowerDetailVO;
    }

    /**
     * 审批借款人
     *
     * @param borrowerApprovalVO 借款人审批信息
     */
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        // 更新借款人认证状态
        Borrower borrower = this.baseMapper.selectById(borrowerApprovalVO.getBorrowerId());
        Integer status = borrowerApprovalVO.getStatus();
        borrower.setStatus(status);
        this.baseMapper.updateById(borrower);

        // 获取用户信息
        Long userId = borrower.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 添加积分
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);

        int curIntegral = userInfo.getIntegral() + borrowerApprovalVO.getInfoIntegral();
        if(borrowerApprovalVO.getIsIdCardOk()) {
            curIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        if(borrowerApprovalVO.getIsHouseOk()) {
            curIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        if(borrowerApprovalVO.getIsCarOk()) {
            curIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        userInfo.setIntegral(curIntegral);

        // 修改审核状态
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        userInfoMapper.updateById(userInfo);
    }
}
