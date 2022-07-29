package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.UserInfo;
import xyz.raysmen.lp.core.pojo.query.UserInfoQuery;
import xyz.raysmen.lp.core.pojo.vo.LoginVO;
import xyz.raysmen.lp.core.pojo.vo.RegisterVO;
import xyz.raysmen.lp.core.pojo.vo.UserIndexVO;
import xyz.raysmen.lp.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface UserInfoService extends IService<UserInfo> {
    /**
     * 获取注册信息来生成用户信息进行注册
     *
     * @param registerVO 注册信息
     */
    void register(RegisterVO registerVO);

    /**
     * 通过登录信息来获取用户信息进行登录，同时记录登录日志
     *
     * @param loginVO   登录信息
     * @param ip        登录者IP地址
     * @return          用户信息
     */
    UserInfoVO login(LoginVO loginVO, String ip);

    /**
     * 根据查询条件获取页面对象
     *
     * @param page          页面对象
     * @param userInfoQuery 查询条件
     * @return              返回含有查询信息的页面对象
     */
    IPage<UserInfo> listPage(Page<UserInfo> page, UserInfoQuery userInfoQuery);

    /**
     * 锁定和解锁用户
     *
     * @param id        用户ID
     * @param status    用户状态
     */
    void lock(Long id, Integer status);

    /**
     * 检查手机号是否已注册
     *
     * @param mobile    手机号
     * @return          返回手机号是否已注册
     */
    boolean checkMobile(String mobile);

    /**
     * 获取首页用户信息
     *
     * @param userId    用户ID
     * @return          返回首页用户信息
     */
    UserIndexVO getIndexUserInfo(Long userId);
}
