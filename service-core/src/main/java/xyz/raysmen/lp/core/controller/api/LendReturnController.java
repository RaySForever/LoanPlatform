package xyz.raysmen.lp.core.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.raysmen.lp.base.util.JwtUtils;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.hfb.HfbConst;
import xyz.raysmen.lp.core.hfb.RequestHelper;
import xyz.raysmen.lp.core.pojo.entity.LendReturn;
import xyz.raysmen.lp.core.service.LendReturnService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/api/core/lendReturn")
public class LendReturnController {
    private final LendReturnService lendReturnService;

    private final ObjectMapper objectMapper;

    @Autowired
    public LendReturnController(LendReturnService lendReturnService, ObjectMapper objectMapper) {
        this.lendReturnService = lendReturnService;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取列表
     *
     * @param lendId 标的id
     */
    @GetMapping("/list/{lendId}")
    public CustomResult list(@PathVariable Long lendId) {
        List<LendReturn> list = lendReturnService.selectByLendId(lendId);
        return CustomResult.ok().data("list", list);
    }

    /**
     * 用户还款
     *
     * @param lendReturnId  还款计划ID
     */
    @PostMapping("/auth/commitReturn/{lendReturnId}")
    public CustomResult commitReturn(@PathVariable Long lendReturnId,
                                     HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        String formStr = lendReturnService.commitReturn(lendReturnId, userId);
        return CustomResult.ok().data("formStr", formStr);
    }

    /**
     * 还款异步回调
     */
    @PostMapping("/notifyUrl")
    public String notifyUrl(HttpServletRequest request) {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        String params = null;
        try {
            params = objectMapper.writeValueAsString(paramMap);
        } catch (JsonProcessingException e) {
            log.error("Jackson序列化异常：JsonProcessingException：{}", ExceptionUtils.getStackTrace(e));
        }
        log.info("还款异步回调：" + params);

        //校验签名
        if(RequestHelper.isSignEquals(paramMap)) {
            if(HfbConst.BORROW_RETURN_SUCCESS_CODE.equals(paramMap.get(HfbConst.RESULT_CODE))) {
                lendReturnService.notify(paramMap);
            } else {
                log.info("还款异步回调失败：" + params);
                return "fail";
            }
        } else {
            log.info("还款异步回调签名错误：" + params);
            return "fail";
        }
        return "success";
    }
}
