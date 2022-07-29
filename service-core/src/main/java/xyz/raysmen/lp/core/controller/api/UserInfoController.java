package xyz.raysmen.lp.core.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import xyz.raysmen.lp.base.util.JwtUtils;
import xyz.raysmen.lp.common.exception.CustomAssert;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.common.util.RegexValidateUtils;
import xyz.raysmen.lp.core.pojo.vo.RegisterVO;
import xyz.raysmen.lp.core.pojo.vo.UserIndexVO;
import xyz.raysmen.lp.core.service.UserInfoService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/api/core/userInfo")
public class UserInfoController {

    private final UserInfoService userInfoService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UserInfoController(UserInfoService userInfoService, RedisTemplate<String, Object> redisTemplate) {
        this.userInfoService = userInfoService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/register")
    public CustomResult register(@RequestBody RegisterVO registerVO) {

        String mobile = registerVO.getMobile();
        String password = registerVO.getPassword();
        String code = registerVO.getCode();

        // MOBILE_NULL_ERROR(-202, "手机号不能为空"),
        CustomAssert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        // MOBILE_ERROR(-203, "手机号不正确"),
        CustomAssert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);
        // 第二次校验，验证手机号是否已注册，已注册即抛出异常
        CustomAssert.isTrue(!userInfoService.checkMobile(mobile), ResponseEnum.MOBILE_EXIST_ERROR);
        // PASSWORD_NULL_ERROR(-204, "密码不能为空"),
        CustomAssert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);
        // CODE_NULL_ERROR(-205, "验证码不能为空"),
        CustomAssert.notEmpty(code, ResponseEnum.CODE_NULL_ERROR);

        // 校验验证码
        String codeFromRedis = (String) redisTemplate.opsForValue().get("lp:sms:code::" + mobile);
        // CODE_ERROR(-206, "验证码不正确"),
        CustomAssert.equals(code, codeFromRedis, ResponseEnum.CODE_ERROR);

        // 注册
        userInfoService.register(registerVO);
        return CustomResult.ok().message("注册成功");
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

    /**
     * 校验手机号是否已注册
     */
    @GetMapping("/checkMobile/{mobile}")
    public boolean checkMobile(@PathVariable("mobile") String mobile) {
        return userInfoService.checkMobile(mobile);
    }

    /**
     * 获取个人空间用户信息
     */
    @GetMapping("/auth/getIndexUserInfo")
    public CustomResult getIndexUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        UserIndexVO userIndexVO = userInfoService.getIndexUserInfo(userId);
        return CustomResult.ok().data("userIndexVO", userIndexVO);
    }
}
