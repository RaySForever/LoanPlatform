package xyz.raysmen.lp.core.pojo.util;

import xyz.raysmen.lp.common.util.MD5;
import xyz.raysmen.lp.core.enums.BorrowerStatusEnum;
import xyz.raysmen.lp.core.enums.UserBindEnum;
import xyz.raysmen.lp.core.pojo.bo.TransFlowBO;
import xyz.raysmen.lp.core.pojo.entity.*;
import xyz.raysmen.lp.core.pojo.vo.*;
import xyz.raysmen.lp.core.util.BigDecimalUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * BeansConverter
 * 对象转换器
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.util
 * @date 2022/06/07 18:03
 */
public final class BeansConverter {
    public static UserInfo registerVOToUserInfo(RegisterVO registerVO) {
        UserInfo userInfo = new UserInfo();

        userInfo.setUserType(registerVO.getUserType());
        userInfo.setEmail(registerVO.getEmail());
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        // 设置一张静态资源服务器上的默认头像图片
        userInfo.setHeadImg(UserInfo.USER_AVATAR);

        return userInfo;
    }

    public static UserInfoVO userInfoToUserInfoVOForToken(UserInfo userInfo, String token, Integer userType) {
        UserInfoVO userInfoVO = new UserInfoVO();

        userInfoVO.setToken(token);
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setHeadImg(userInfo.getHeadImg());
        userInfoVO.setMobile(userInfo.getMobile());
        userInfoVO.setUserType(userType);

        return userInfoVO;
    }

    public static void userBindVOToUserBind(UserBindVO userBindVO, UserBind userBind) {
        userBind.setBankNo(userBindVO.getBankNo());
        userBind.setBankType(userBindVO.getBankType());
        userBind.setIdCard(userBindVO.getIdCard());
        userBind.setMobile(userBindVO.getMobile());
        userBind.setName(userBindVO.getName());
    }

    public static void userBindToUserInfoForBind(UserBind userBind, UserInfo userInfo, String bindCode, Integer status) {
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
    }

    public static Borrower borrowerVOToBorrowerForSave(BorrowerVO borrowerVO, UserInfo userInfo, Long userId) {
        Borrower borrower = new Borrower();
        borrower.setAge(borrowerVO.getAge());
        borrower.setSex(borrowerVO.getSex());
        borrower.setEducation(borrowerVO.getEducation());
        borrower.setMarry(borrowerVO.getMarry());
        borrower.setIndustry(borrowerVO.getIndustry());
        borrower.setIncome(borrowerVO.getIncome());
        borrower.setReturnSource(borrowerVO.getReturnSource());
        borrower.setContactsName(borrowerVO.getContactsName());
        borrower.setContactsMobile(borrowerVO.getContactsMobile());
        borrower.setContactsRelation(borrowerVO.getContactsRelation());
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());

        return borrower;
    }

    public static void borrowerToBorrowerDetailVO(Borrower borrower, BorrowerDetailVO borrowerDetailVO) {
        borrowerDetailVO.setUserId(borrower.getUserId());
        borrowerDetailVO.setName(borrower.getName());
        borrowerDetailVO.setIdCard(borrower.getIdCard());
        borrowerDetailVO.setMobile(borrower.getMobile());
        borrowerDetailVO.setAge(borrower.getAge());
        borrowerDetailVO.setContactsName(borrower.getContactsName());
        borrowerDetailVO.setContactsMobile(borrower.getContactsMobile());
        borrowerDetailVO.setCreateTime(borrower.getCreateTime());
        // 婚否
        borrowerDetailVO.setMarry(borrower.getMarry() ? "是" : "否");
        // 性别
        borrowerDetailVO.setSex(borrower.getSex() == 1 ? "男" : "女");
        // 审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        borrowerDetailVO.setStatus(status);
    }

    public static void borrowerAttachTOBorrowerAttachVO(BorrowerAttach borrowerAttach, BorrowerAttachVO borrowerAttachVO) {
        borrowerAttachVO.setImageType(borrowerAttach.getImageType());
        borrowerAttachVO.setImageUrl(borrowerAttach.getImageUrl());
    }

    public static void borrowInfoApprovalVOAndBorrowInfoToLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo, Lend lend) {
        lend.setUserId(borrowInfo.getUserId());
        lend.setBorrowInfoId(borrowInfo.getId());
        lend.setTitle(borrowInfoApprovalVO.getTitle());
        BigDecimal amount = borrowInfo.getAmount();
        lend.setAmount(amount);
        Integer period = borrowInfo.getPeriod();
        lend.setPeriod(period);
        // 从审批对象中获取
        lend.setLendYearRate(BigDecimalUtils.percentToDecimal(borrowInfoApprovalVO.getLendYearRate()));
        BigDecimal serviceRate = BigDecimalUtils.percentToDecimal(borrowInfoApprovalVO.getServiceRate());
        lend.setServiceRate(serviceRate);

        lend.setReturnMethod(borrowInfo.getReturnMethod());

        // 起息日期
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate lendStartDate = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate(), dtf);
        lend.setLendStartDate(lendStartDate);

        // 结束日期
        LocalDate lendEndDate = lendStartDate.plusMonths(period);
        lend.setLendEndDate(lendEndDate);

        //描述
        lend.setLendInfo(borrowInfoApprovalVO.getLendInfo());

        // 平台预期收益
        // 月年化 = 年化 / 12
        BigDecimal monthRate = BigDecimalUtils.getMonthRate(serviceRate);
        // 平台收益 = 标的金额 * 月年化 * 期数
        BigDecimal expectAmount = BigDecimalUtils.getExpectAmount(amount, monthRate, new BigDecimal(period));
        lend.setExpectAmount(expectAmount);
    }

    public static void userInfoAndTransFlowBOToTransFlow(UserInfo userInfo, TransFlowBO transFlowBO, TransFlow transFlow) {
        transFlow.setUserId(userInfo.getId());
        transFlow.setUserName(userInfo.getName());
        transFlow.setTransNo(transFlowBO.getAgentBillNo());
        transFlow.setTransType(transFlowBO.getTransTypeEnum().getTransType());
        transFlow.setTransTypeName(transFlowBO.getTransTypeEnum().getTransTypeName());
        transFlow.setTransAmount(transFlowBO.getAmount());
        transFlow.setMemo(transFlowBO.getMemo());
    }

    public static void lendAndInvestVOToLendItem(Lend lend, InvestVO investVO, LendItem lendItem) {
        // 投资人名字
        lendItem.setInvestName(investVO.getInvestName());
        // 对应的标的id
        lendItem.setLendId(investVO.getLendId());
        // 此笔投资金额
        lendItem.setInvestAmount(new BigDecimal(investVO.getInvestAmount()));
        // 年化
        lendItem.setLendYearRate(lend.getLendYearRate());
        // 开始时间
        lendItem.setLendStartDate(lend.getLendStartDate());
        // 结束时间
        lendItem.setLendEndDate(lend.getLendEndDate());
    }

    public static void userInfoUserAccountUserLoginRecordToUserIndexVO(UserInfo userInfo, UserAccount userAccount, UserLoginRecord userLoginRecord, UserIndexVO userIndexVO) {
        userIndexVO.setUserId(userInfo.getId());
        userIndexVO.setUserType(userInfo.getUserType());
        userIndexVO.setName(userInfo.getName());
        userIndexVO.setNickName(userInfo.getNickName());
        userIndexVO.setHeadImg(userInfo.getHeadImg());
        userIndexVO.setBindStatus(userInfo.getBindStatus());
        userIndexVO.setAmount(userAccount.getAmount());
        userIndexVO.setFreezeAmount(userAccount.getFreezeAmount());
        userIndexVO.setLastLoginTime(userLoginRecord.getCreateTime());
    }
}
