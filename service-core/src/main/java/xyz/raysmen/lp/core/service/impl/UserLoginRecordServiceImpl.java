package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import xyz.raysmen.lp.core.pojo.entity.UserLoginRecord;
import xyz.raysmen.lp.core.mapper.UserLoginRecordMapper;
import xyz.raysmen.lp.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

    /**
     * 根据用户ID获取前50个登录日志
     *
     * @param userId 用户ID
     * @return 用户登录日志列表
     */
    @Override
    public List<UserLoginRecord> listTop50(Long userId) {
        LambdaQueryWrapper<UserLoginRecord> wrapper = new LambdaQueryWrapper<UserLoginRecord>()
                .eq(UserLoginRecord::getUserId, userId)
                .orderByDesc(UserLoginRecord::getId)
                .last("limit 50");
        return this.baseMapper.selectList(wrapper);
    }
}
