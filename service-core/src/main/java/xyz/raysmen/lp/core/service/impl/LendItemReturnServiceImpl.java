package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import xyz.raysmen.lp.core.pojo.entity.LendItemReturn;
import xyz.raysmen.lp.core.mapper.LendItemReturnMapper;
import xyz.raysmen.lp.core.service.LendItemReturnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 标的出借回款记录表 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Service
public class LendItemReturnServiceImpl extends ServiceImpl<LendItemReturnMapper, LendItemReturn> implements LendItemReturnService {

    /**
     * 获取标的出借回款记录列表
     *
     * @param lendId 标的编号
     * @param userId 用户ID
     * @return 返回标的出借回款记录列表
     */
    @Override
    public List<LendItemReturn> selectByLendId(Long lendId, Long userId) {
        LambdaQueryWrapper<LendItemReturn> wrapper = new LambdaQueryWrapper<LendItemReturn>()
                .eq(LendItemReturn::getLendId, lendId)
                .eq(LendItemReturn::getInvestUserId, userId)
                .orderByAsc(LendItemReturn::getCurrentPeriod);
        return this.baseMapper.selectList(wrapper);
    }
}
