package xyz.raysmen.lp.sms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.raysmen.lp.sms.client.fallback.CoreUserInfoClientFallback;

/**
 * CoreUserInfoClient
 * 远程调用核心服务的userinfocontroller的feign服务接口
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.sms.client
 * @date 2022/07/08 22:20
 */
@Component
@FeignClient(value = "service-core", fallback = CoreUserInfoClientFallback.class)
public interface CoreUserInfoClient {
    /**
     * 校验手机号是否已注册
     *
     * @param mobile    手机号
     * @return          返回手机号是否已注册
     */
    @GetMapping("/api/core/userInfo/checkMobile/{mobile}")
    boolean checkMobile(@PathVariable("mobile") String mobile);
}
