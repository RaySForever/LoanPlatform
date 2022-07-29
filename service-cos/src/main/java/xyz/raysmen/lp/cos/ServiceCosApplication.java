package xyz.raysmen.lp.cos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import xyz.raysmen.lp.cos.util.CosProperties;

/**
 * ServiceCosApplication
 * 对象存储服务启动类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.cos
 * @date 2022/06/01 20:35
 */
@EnableDiscoveryClient
@EnableConfigurationProperties(CosProperties.class)
@SpringBootApplication(scanBasePackages =
        {"xyz.raysmen.lp.cos",
        "xyz.raysmen.lp.base",
        "xyz.raysmen.lp.common"}
)
public class ServiceCosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCosApplication.class, args);
    }
}
