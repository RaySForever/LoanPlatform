package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.UserLoginRecord;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {
    /**
     * 根据用户ID获取前50个登录日志
     *
     * @param userId    用户ID
     * @return          用户登录日志列表
     */
    List<UserLoginRecord> listTop50(Long userId);
}
