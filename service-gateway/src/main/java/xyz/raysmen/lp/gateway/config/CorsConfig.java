package xyz.raysmen.lp.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfig
 * 跨域配置类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.gateway.config
 * @date 2022/07/10 17:44
 */
@Configuration(proxyBeanMethods = false)
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsFilter() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 为所有访问路径注册以上跨域配置
                registry.addMapping("/**")
                        // 是否允许携带cookie
                        .allowCredentials(true)
                        // 5.3.x的要求：可接受的域，是一个具体域名或者*（代表任意域名）
                        .allowedOriginPatterns("*")
                        // 允许携带的头
                        .allowedHeaders("*")
                        // 允许访问的方式
                        .allowedMethods("*");
            }
        };
    }
}
