package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.LendItemReturn;

import java.util.List;

/**
 * <p>
 * 标的出借回款记录表 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface LendItemReturnService extends IService<LendItemReturn> {
    /**
     * 获取标的出借回款记录列表
     *
     * @param lendId    标的编号
     * @param userId    用户ID
     * @return          返回标的出借回款记录列表
     */
    List<LendItemReturn> selectByLendId(Long lendId, Long userId);
}
