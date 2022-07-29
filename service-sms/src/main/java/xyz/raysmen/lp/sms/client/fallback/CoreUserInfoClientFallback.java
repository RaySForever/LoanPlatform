package xyz.raysmen.lp.sms.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.raysmen.lp.sms.client.CoreUserInfoClient;

/**
 * CoreUserInfoClientFallback
 * 针对CoreUserInfoClient的容错回调类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.sms.client.fallback
 * @date 2022/07/09 15:24
 */
@Slf4j
@Service
public class CoreUserInfoClientFallback implements CoreUserInfoClient {
    /**
     * 校验手机号是否已注册，无法远程调用时的替代方法
     * 默认该手机号未注册，使得用户得以进行注册操作，注册时还会进行验证
     *
     * @param mobile 手机号
     * @return 返回手机号是否已注册
     */
    @Override
    public boolean checkMobile(String mobile) {
        log.error("远程调用失败，服务熔断");
        return false;
    }
}
