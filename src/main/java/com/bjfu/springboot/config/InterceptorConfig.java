package com.bjfu.springboot.config;

import com.bjfu.springboot.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")  // Intercept all requests, determine if login is required by checking if the token is valid
                .excludePathPatterns("/user/login", "/user/register", "/**/export", "/**/import", "/file/**","/dataupload/**",
                        "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**", "/api", "/api-docs", "/api-docs/**")
                .excludePathPatterns( "/**/*.html", "/**/*.js", "/**/*.css", "/**/*.woff", "/**/*.ttf");  // Exclude static files from being intercepted

    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }

}
