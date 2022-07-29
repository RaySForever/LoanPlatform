package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.UserBind;
import xyz.raysmen.lp.core.pojo.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface UserBindService extends IService<UserBind> {
    /**
     * 账户绑定提交到托管平台的数据
     * @param userBindVO    账户绑定信息
     * @param userId        用户ID
     * @return              返回构建的充值自动提交表单字符串
     */
    String commitBindUser(UserBindVO userBindVO, Long userId);

    /**
     * 依据传入的参数表修改绑定状态
     *
     * @param paramMap  传入的参数表
     */
    void notify(Map<String, Object> paramMap);

    /**
     * 通过用户ID获取绑定协议号
     *
     * @param userId    用户ID
     * @return          返回绑定协议号
     */
    String getBindCodeByUserId(Long userId);
}
