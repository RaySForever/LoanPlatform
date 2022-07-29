package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.raysmen.lp.core.mapper.TransFlowMapper;
import xyz.raysmen.lp.core.mapper.UserInfoMapper;
import xyz.raysmen.lp.core.pojo.bo.TransFlowBO;
import xyz.raysmen.lp.core.pojo.entity.TransFlow;
import xyz.raysmen.lp.core.pojo.entity.UserInfo;
import xyz.raysmen.lp.core.pojo.util.BeansConverter;
import xyz.raysmen.lp.core.service.TransFlowService;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    private final UserInfoMapper userInfoMapper;

    @Autowired
    public TransFlowServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    /**
     * 保存交易流水业务
     *
     * @param transFlowBO 交易流水业务
     */
    @Override
    public void saveTransFlow(TransFlowBO transFlowBO) {
        // 获取用户基本信息 user_info
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getBindCode, transFlowBO.getBindCode());
        UserInfo userInfo = userInfoMapper.selectOne(wrapper);

        // 存储交易流水数据
        TransFlow transFlow = new TransFlow();
        BeansConverter.userInfoAndTransFlowBOToTransFlow(userInfo, transFlowBO, transFlow);
        this.baseMapper.insert(transFlow);
    }

    /**
     * 根据客户充值订单号是否已存在来判断流水存在与否
     *
     * @param agentBillNo 客户充值订单号
     * @return 返回客户充值订单号是否已存在
     */
    @Override
    public boolean isSaveTransFlow(String agentBillNo) {
        LambdaQueryWrapper<TransFlow> wrapper = new LambdaQueryWrapper<TransFlow>()
                .eq(TransFlow::getTransNo, agentBillNo);
        Long count = this.baseMapper.selectCount(wrapper);
        return count > 0;
    }

    /**
     * 通过用户ID获取交易流水业务列表
     *
     * @param userId 用户ID
     * @return 返回交易流水业务列表
     */
    @Override
    public List<TransFlow> selectByUserId(Long userId) {
        LambdaQueryWrapper<TransFlow> wrapper = new LambdaQueryWrapper<TransFlow>()
                .eq(TransFlow::getUserId, userId)
                .orderByDesc(TransFlow::getId);
        return this.baseMapper.selectList(wrapper);
    }
}
