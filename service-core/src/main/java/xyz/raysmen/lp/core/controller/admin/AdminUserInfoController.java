package xyz.raysmen.lp.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.raysmen.lp.base.util.JwtUtils;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.core.pojo.entity.UserInfo;
import xyz.raysmen.lp.core.pojo.query.UserInfoQuery;
import xyz.raysmen.lp.core.service.UserInfoService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员管理 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/admin/core/userInfo")
public class AdminUserInfoController {

    private final UserInfoService userInfoService;

    @Autowired
    public AdminUserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * 获取会员分页列表
     *
     * @param current       当前页码
     * @param size          每页记录数
     * @param userInfoQuery 用户信息搜索条件
     * @return              返回带有页面对象数据的统一返回结果
     */
    @GetMapping("/list/{current}/{size}")
    public CustomResult listPage(@PathVariable Long current,
                                 @PathVariable Long size,
                                 UserInfoQuery userInfoQuery) {
        Page<UserInfo> page = new Page<>(current, size);
        IPage<UserInfo> pageModel = userInfoService.listPage(page, userInfoQuery);
        return CustomResult.ok().data("pageModel", pageModel);
    }

    /**
     * 校验令牌
     */
    @GetMapping("/checkToken")
    public CustomResult checkToken(HttpServletRequest request) {
        String token = request.getHeader("token");

        if (JwtUtils.checkToken(token)) {
            return CustomResult.ok();
        } else {
            // 警告用户未登录；LOGIN_AUTH_ERROR(-213, "未登录"),
            return CustomResult.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }
}
