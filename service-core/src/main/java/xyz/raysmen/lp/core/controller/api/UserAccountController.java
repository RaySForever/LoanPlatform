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
import xyz.raysmen.lp.core.service.UserAccountService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 前端控制器
 * </p>
 * 会员账户
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/api/core/userAccount")
public class UserAccountController {
    private final UserAccountService userAccountService;

    private final ObjectMapper objectMapper;

    private final String successResultCode = "0001";

    @Autowired
    public UserAccountController(UserAccountService userAccountService, ObjectMapper objectMapper) {
        this.userAccountService = userAccountService;
        this.objectMapper = objectMapper;
    }

    /**
     * 充值
     *
     * @param chargeAmt 充值金额
     */
    @PostMapping("/auth/commitCharge/{chargeAmt}")
    public CustomResult commitCharge(@PathVariable BigDecimal chargeAmt, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String formStr = userAccountService.commitCharge(chargeAmt, userId);
        return CustomResult.ok().data("formStr", formStr);
    }

    /**
     * 用户充值异步回调
     */
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        String str = null;
        try {
            str = objectMapper.writeValueAsString(paramMap);
        } catch (JsonProcessingException e) {
            log.error("Jackson序列化异常：JsonProcessingException：{}", ExceptionUtils.getStackTrace(e));
        }
        log.info("用户充值异步回调：" + str);

        // 校验签名
        if(RequestHelper.isSignEquals(paramMap)) {
            // 判断充值是否成功
            if(HfbConst.RECHARGE_SUCCESS_CODE.equals(paramMap.get(HfbConst.RESULT_CODE))) {
                // 同步账户数据
                return userAccountService.notify(paramMap);
            } else {
                log.info("用户充值异步回调充值失败：" + str);
                return "fail";
            }
        } else {
            log.info("用户充值异步回调签名错误：" + str);
            return "fail";
        }
    }

    /**
     * 查询账户余额
     */
    @GetMapping("/auth/getAccount")
    public CustomResult getAccount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        BigDecimal account = userAccountService.getAccount(userId);
        return CustomResult.ok().data("account", account);
    }

    @PostMapping("/auth/commitWithdraw/{fetchAmt}")
    public CustomResult commitWithdraw(@PathVariable BigDecimal fetchAmt,
                                       HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        // 返回构建的提现提交表单字符串
        String formStr = userAccountService.commitWithdraw(fetchAmt, userId);
        return CustomResult.ok().data("formStr", formStr);
    }

    /**
     * 用户提现异步回调
     */
    @PostMapping("/notifyWithdraw")
    public String notifyWithdraw(HttpServletRequest request) {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        String params = null;
        try {
            params = objectMapper.writeValueAsString(paramMap);
        } catch (JsonProcessingException e) {
            log.error("Jackson序列化异常：JsonProcessingException：{}", ExceptionUtils.getStackTrace(e));
        }
        log.info("提现异步回调：" + params);

        //校验签名
        if(RequestHelper.isSignEquals(paramMap)) {
            //提现成功交易
            if(HfbConst.WITHDRAW_SUCCESS_CODE.equals(paramMap.get(HfbConst.RESULT_CODE))) {
                userAccountService.notifyWithdraw(paramMap);
            } else {
                log.info("提现异步回调充值失败：" + params);
                return "fail";
            }
        } else {
            log.info("提现异步回调签名错误：" + params);
            return "fail";
        }
        return "success";
    }
}
