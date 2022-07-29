package xyz.raysmen.lp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ServiceCoreApplication
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp
 * @date 2022/05/17 22:04
 * @description 模块service-core启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ServiceCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCoreApplication.class, args);
    }
}
