package xyz.raysmen.lp.sms.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenFeignConfig
 * Feign配置日志类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.sms.config
 * @date 2022/07/08 23:49
 */
@Configuration
public class OpenFeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
