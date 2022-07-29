package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.raysmen.lp.common.exception.CustomAssert;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.core.enums.UserBindEnum;
import xyz.raysmen.lp.core.hfb.FormHelper;
import xyz.raysmen.lp.core.hfb.HfbConst;
import xyz.raysmen.lp.core.hfb.RequestHelper;
import xyz.raysmen.lp.core.mapper.UserBindMapper;
import xyz.raysmen.lp.core.mapper.UserInfoMapper;
import xyz.raysmen.lp.core.pojo.entity.UserBind;
import xyz.raysmen.lp.core.pojo.entity.UserInfo;
import xyz.raysmen.lp.core.pojo.util.BeansConverter;
import xyz.raysmen.lp.core.pojo.vo.UserBindVO;
import xyz.raysmen.lp.core.service.UserBindService;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {
    private final UserInfoMapper userInfoMapper;

    @Autowired
    public UserBindServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    /**
     * 账户绑定提交到托管平台的数据
     *
     * @param userBindVO 账户绑定信息
     * @param userId     用户ID
     * @return 返回构建的充值自动提交表单字符串
     */
    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {
        // 查询身份证号码是否绑定
        LambdaQueryWrapper<UserBind> wrapper = new LambdaQueryWrapper<UserBind>()
                .eq(UserBind::getIdCard, userBindVO.getIdCard())
                .ne(UserBind::getUserId, userId);
        UserBind userBind = this.baseMapper.selectOne(wrapper);
        // USER_BIND_IDCARD_EXIST_ERROR(-301, "身份证号码已绑定"),
        CustomAssert.isNull(userBind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);

        // 查询用户绑定信息
        wrapper.clear();
        userBind = this.baseMapper.selectOne(wrapper.eq(UserBind::getUserId, userId));

        // 判断是否有绑定记录
        if (userBind == null) {
            // 如果未创建绑定记录，则创建一条记录
            userBind = new UserBind();
            BeansConverter.userBindVOToUserBind(userBindVO, userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            this.baseMapper.insert(userBind);
        } else {
            // 曾经跳转到托管平台，但是未操作完成，此时将用户最新填写的数据同步到userBind对象
            BeansConverter.userBindVOToUserBind(userBindVO, userBind);
            this.baseMapper.updateById(userBind);
        }

        Map<String, Object> paramMap = getParamMap(userBindVO, userId);

        // 构建充值自动提交表单
        return FormHelper.buildForm(HfbConst.USERBIND_URL, paramMap);
    }

    /**
     * 依据传入的参数表修改绑定状态
     *
     * @param paramMap 传入的参数表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notify(Map<String, Object> paramMap) {
        // 获取绑定码
        String bindCode = (String) paramMap.get("bindCode");
        // 获取会员id
        String agentUserId = (String) paramMap.get("agentUserId");

        // 更新用户绑定表
        LambdaQueryWrapper<UserBind> wrapper = new LambdaQueryWrapper<UserBind>()
                .eq(UserBind::getUserId, agentUserId);
        UserBind userBind = this.baseMapper.selectOne(wrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        this.baseMapper.updateById(userBind);

        // 更新用户表
        UserInfo userInfo = userInfoMapper.selectById(agentUserId);
        BeansConverter.userBindToUserInfoForBind(userBind, userInfo, bindCode, UserBindEnum.BIND_OK.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 通过用户ID获取绑定协议号
     *
     * @param userId 用户ID
     * @return 返回绑定协议号
     */
    @Override
    public String getBindCodeByUserId(Long userId) {
        LambdaQueryWrapper<UserBind> wrapper = new LambdaQueryWrapper<UserBind>()
                .eq(UserBind::getUserId, userId);
        UserBind userBind = baseMapper.selectOne(wrapper);
        return userBind.getBindCode();
    }

    private Map<String, Object> getParamMap(UserBindVO userBindVO, Long userId) {
        Map<String, Object> paramMap = new HashMap<>(11);

        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard",userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        return paramMap;
    }
}
