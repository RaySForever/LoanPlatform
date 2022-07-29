package xyz.raysmen.lp.sms.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import xyz.raysmen.lp.common.exception.CustomAssert;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.common.util.RandomUtils;
import xyz.raysmen.lp.common.util.RegexValidateUtils;
import xyz.raysmen.lp.sms.client.CoreUserInfoClient;
import xyz.raysmen.lp.sms.service.SmsService;

import java.util.concurrent.TimeUnit;

/**
 * ApiSmsController
 * 验证码短信 前端控制器类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.sms.controller
 * @date 2022/05/31 21:51
 */
@Slf4j
@RestController
@RequestMapping("/api/sms")
public class ApiSmsController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final SmsService smsService;
    private final CoreUserInfoClient coreUserInfoClient;

    @Autowired
    public ApiSmsController(RedisTemplate<String, Object> redisTemplate, SmsService smsService, CoreUserInfoClient coreUserInfoClient) {
        this.redisTemplate = redisTemplate;
        this.smsService = smsService;
        this.coreUserInfoClient = coreUserInfoClient;
    }

    @GetMapping("/send/{mobile}")
    public CustomResult sendSms(@PathVariable String mobile) {
        // 验证手机号非空，格式正常
        CustomAssert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        CustomAssert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        // 验证手机号是否注册，已注册即抛出异常
        boolean checkMobile = coreUserInfoClient.checkMobile(mobile);
        CustomAssert.isTrue(!checkMobile, ResponseEnum.MOBILE_EXIST_ERROR);

        // 生成验证码
        String code = RandomUtils.getSixBitRandom();

        // 发送短信
        // 测试时不需要发送短信，直接在Redis内查看即可
        //smsService.send(mobile, code);

        // 向Redis存储手机号与验证码5分钟
        // 实际设置的键为 lp:sms:code::#{mobile}
        redisTemplate.opsForValue().set("lp:sms:code" + mobile, code, 5L, TimeUnit.MINUTES);
        return CustomResult.ok().message("短信发送成功");
    }
}
