package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.raysmen.lp.base.util.JwtUtils;
import xyz.raysmen.lp.common.exception.CustomAssert;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.common.util.MD5;
import xyz.raysmen.lp.core.mapper.UserAccountMapper;
import xyz.raysmen.lp.core.mapper.UserInfoMapper;
import xyz.raysmen.lp.core.mapper.UserLoginRecordMapper;
import xyz.raysmen.lp.core.pojo.entity.UserAccount;
import xyz.raysmen.lp.core.pojo.entity.UserInfo;
import xyz.raysmen.lp.core.pojo.entity.UserLoginRecord;
import xyz.raysmen.lp.core.pojo.query.UserInfoQuery;
import xyz.raysmen.lp.core.pojo.util.BeansConverter;
import xyz.raysmen.lp.core.pojo.vo.LoginVO;
import xyz.raysmen.lp.core.pojo.vo.RegisterVO;
import xyz.raysmen.lp.core.pojo.vo.UserIndexVO;
import xyz.raysmen.lp.core.pojo.vo.UserInfoVO;
import xyz.raysmen.lp.core.service.UserInfoService;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    private final UserAccountMapper userAccountMapper;
    private final UserLoginRecordMapper userLoginRecordMapper;

    @Autowired
    public UserInfoServiceImpl(UserAccountMapper userAccountMapper, UserLoginRecordMapper userLoginRecordMapper) {
        this.userAccountMapper = userAccountMapper;
        this.userLoginRecordMapper = userLoginRecordMapper;
    }

    /**
     * 获取注册信息来生成用户信息进行注册
     *
     * @param registerVO 注册信息
     */
    @CacheEvict(value = "lp:core:check:mobile", key = "#registerVO.mobile")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {
        // 判断用户是否被注册
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getMobile, registerVO.getMobile());
        Long count = this.baseMapper.selectCount(queryWrapper);
        // MOBILE_EXIST_ERROR(-207, "手机号已被注册"),
        CustomAssert.isTrue(count == 0L, ResponseEnum.MOBILE_EXIST_ERROR);

        // 插入用户基本信息
        UserInfo userInfo = BeansConverter.registerVOToUserInfo(registerVO);
        this.baseMapper.insert(userInfo);

        // 创建会员账户
        UserAccount userAccount = new UserAccount();
        UserInfo registeredUserInfo = this.baseMapper.selectOne(queryWrapper);
        userAccount.setUserId(registeredUserInfo.getId());
        this.userAccountMapper.insert(userAccount);
    }

    /**
     * 通过登录信息来获取用户信息进行登录，同时记录登录日志
     *
     * @param loginVO 登录信息
     * @param ip      登录者IP地址
     * @return 用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Integer userType = loginVO.getUserType();

        // 获取会员
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getMobile, mobile)
                .eq(UserInfo::getUserType, userType);
        UserInfo userInfo = this.baseMapper.selectOne(wrapper);

        // 用户不存在
        // LOGIN_MOBILE_ERROR(-210, "用户不存在"),
        CustomAssert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);

        // 校验密码
        // LOGIN_PASSWORD_ERROR(-211, "密码错误"),
        CustomAssert.equals(MD5.encrypt(password), userInfo.getPassword(), ResponseEnum.LOGIN_PASSWORD_ERROR);

        // 用户是否被禁用
        // LOGIN_DISABLED_ERROR(-212, "用户被锁定"),
        CustomAssert.equals(userInfo.getUserType(), UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);

        //记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        this.userLoginRecordMapper.insert(userLoginRecord);

        //生成token
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());

        return BeansConverter.userInfoToUserInfoVOForToken(userInfo, token, userType);
    }

    /**
     * 根据查询条件获取页面对象
     *
     * @param page          页面对象
     * @param userInfoQuery 查询条件
     * @return 返回含有查询信息的页面对象
     */
    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> page, UserInfoQuery userInfoQuery) {
        if (userInfoQuery == null) {
            return this.baseMapper.selectPage(page, null);
        }

        String mobile = userInfoQuery.getMobile();
        Integer status = userInfoQuery.getStatus();
        Integer userType = userInfoQuery.getUserType();

        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<UserInfo>()
                .eq(StringUtils.isNotBlank(mobile), UserInfo::getMobile, mobile)
                .eq(status != null, UserInfo::getStatus, status)
                .eq(userType != null, UserInfo::getUserType, userType);

        return this.baseMapper.selectPage(page, wrapper);
    }

    /**
     * 锁定和解锁用户
     *
     * @param id     用户ID
     * @param status 用户状态
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);

        this.baseMapper.updateById(userInfo);
    }

    /**
     * 检查手机号是否已注册
     *
     * @param mobile 手机号
     * @return 返回手机号是否已注册
     */
    @Cacheable(value = "lp:core:check:mobile", key = "#mobile")
    @Override
    public boolean checkMobile(String mobile) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        // 到数据库查该手机号有几个
        Long count = this.baseMapper.selectCount(wrapper.eq(UserInfo::getMobile, mobile));
        return count > 0;
    }

    /**
     * 获取首页用户信息
     *
     * @param userId 用户ID
     * @return 返回首页用户信息
     */
    @Override
    public UserIndexVO getIndexUserInfo(Long userId) {
        // 用户信息
        UserInfo userInfo = baseMapper.selectById(userId);

        // 账户信息
        LambdaQueryWrapper<UserAccount> userAccountQueryWrapper = new LambdaQueryWrapper<UserAccount>()
                .eq(UserAccount::getUserId, userId);
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);

        // 登录信息
        LambdaQueryWrapper<UserLoginRecord> userLoginRecordQueryWrapper = new LambdaQueryWrapper<UserLoginRecord>()
                .eq(UserLoginRecord::getUserId, userId)
                .orderByDesc(UserLoginRecord::getId)
                .last("limit 1");
        UserLoginRecord userLoginRecord = userLoginRecordMapper.selectOne(userLoginRecordQueryWrapper);

        // 组装结果数据
        UserIndexVO userIndexVO = new UserIndexVO();
        BeansConverter.userInfoUserAccountUserLoginRecordToUserIndexVO(
                userInfo, userAccount, userLoginRecord, userIndexVO);

        return userIndexVO;
    }
}
