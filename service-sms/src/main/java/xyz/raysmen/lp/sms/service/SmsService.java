package xyz.raysmen.lp.sms.service;

/**
 * SmsService
 * 短信服务类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.sms.service
 * @date 2022/05/31 20:13
 */
public interface SmsService {
    /**
     * 发送腾讯云短信
     *
     * @param mobile 手机号
     * @param param  短信模板参数，多为验证码，且仅有一个
     */
    void send(String mobile, String... param);
}
