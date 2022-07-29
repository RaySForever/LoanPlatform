package xyz.raysmen.lp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ServiceGatewayApplication
 * api网关服务
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.gateway
 * @date 2022/07/10 17:11
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ServiceGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceGatewayApplication.class, args);
    }
}
