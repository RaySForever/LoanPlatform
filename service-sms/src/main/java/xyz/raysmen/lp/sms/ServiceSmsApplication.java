package xyz.raysmen.lp.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ServiceSmsApplication
 * 短信微服务启动类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.sms
 * @date 2022/05/31 19:52
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages =
        {"xyz.raysmen.lp.sms",
         "xyz.raysmen.lp.base",
         "xyz.raysmen.lp.common"}
)
public class ServiceSmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSmsApplication.class, args);
    }
}
