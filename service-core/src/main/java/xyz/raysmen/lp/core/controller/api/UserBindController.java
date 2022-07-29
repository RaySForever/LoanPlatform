package xyz.raysmen.lp.core.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.raysmen.lp.base.util.JwtUtils;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.hfb.RequestHelper;
import xyz.raysmen.lp.core.pojo.vo.UserBindVO;
import xyz.raysmen.lp.core.service.UserBindService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 * 会员账号绑定
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/api/core/userBind")
public class UserBindController {
    private final UserBindService userBindService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserBindController(UserBindService userBindService, ObjectMapper objectMapper) {
        this.userBindService = userBindService;
        this.objectMapper = objectMapper;
    }

    /**
     * 账户绑定提交数据
     */
    @PostMapping("/auth/bind")
    public CustomResult bind(@RequestBody UserBindVO userBindVO, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String formStr = userBindService.commitBindUser(userBindVO, userId);
        return CustomResult.ok().data("formStr", formStr);
    }

    /**
     * 账户绑定异步回调
     */
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        String str = null;
        try {
            str = objectMapper.writeValueAsString(paramMap);
        } catch (JsonProcessingException e) {
            log.error("Jackson序列化异常：JsonProcessingException");
            e.printStackTrace();
        }
        log.info("用户账号绑定异步回调：" + str);

        // 校验签名
        if (!RequestHelper.isSignEquals(paramMap)) {
            log.error("用户账号绑定异步回调签名错误：" + str);
            return "fail";
        }

        // 修改绑定状态
        userBindService.notify(paramMap);

        return "success";
    }
}
