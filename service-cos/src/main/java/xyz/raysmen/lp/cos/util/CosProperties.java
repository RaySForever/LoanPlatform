package xyz.raysmen.lp.cos.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CosProperties
 * 腾讯云对象储存服务API常量读取工具类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.cos.util
 * @date 2022/06/01 20:17
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "tencentcloud.cos")
public class CosProperties implements InitializingBean {
    /**
     * 腾讯云账户密钥对，ID
     */
    private String secretId;
    /**
     * 腾讯云账户密钥对，键
     */
    private String secretKey;
    /**
     * 指定地域
     */
    private String region;
    /**
     * 存储桶名称
     */
    private String bucketName;
    /**
     * 文件上传到 COS 上的路径，以“,”分割多个路径
     */
    private String keys;

    public static String SECRET_ID;
    public static String SECRET_KEY;
    public static String REGION;
    public static String BUCKET_NAME;
    public static String[] KEYS;

    /**
     * 当私有成员被赋值后，此方法自动被调用，从而初始化常量
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        SECRET_ID = this.secretId;
        SECRET_KEY = this.secretKey;
        REGION = this.region;
        BUCKET_NAME = this.bucketName;
        KEYS = this.keys.split(",");
    }
}
