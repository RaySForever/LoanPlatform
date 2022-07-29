package xyz.raysmen.lp.sms.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SmsProperties
 * 腾讯云短信API常量读取工具类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.sms.util
 * @date 2022/05/31 20:01
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "tencentcloud.sms")
public class SmsProperties implements InitializingBean {
    /**
     * 腾讯云账户密钥对，ID
     */
    private String secretId;
    /**
     * 腾讯云账户密钥对，键
     */
    private String secretKey;
    /**
     * 指定接入地域域名
     */
    private String endpointDomain;
    /**
     * 指定地域
     */
    private String region;
    /**
     * 短信应用ID
     */
    private String sdkAppId;
    /**
     * 短信签名内容
     */
    private String signName;
    /**
     * 模板 ID
     */
    private String templateId;

    public static String SECRET_ID;
    public static String SECRET_KEY;
    public static String ENDPOINT_DOMAIN;
    public static String REGION;
    public static String SDK_APP_ID;
    public static String SIGN_NAME;
    public static String TEMPLATE_ID;

    /**
     * 当私有成员被赋值后，此方法自动被调用，从而初始化常量
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        SECRET_ID = this.secretId;
        SECRET_KEY = this.secretKey;
        ENDPOINT_DOMAIN = this.endpointDomain;
        REGION = this.region;
        SDK_APP_ID = this.sdkAppId;
        SIGN_NAME = this.signName;
        TEMPLATE_ID = this.templateId;
    }
}
