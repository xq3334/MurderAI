package com.codexlab.aimurder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 层配置。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 为本地开发、局域网访问和内网穿透访问开启跨域配置。
     *
     * @param registry Spring 的跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "http://*.cpolar.top",
                        "https://*.cpolar.top"
                )
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
