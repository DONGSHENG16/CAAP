package com.bjfu.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    // The maximum effective duration for cross-origin requests. Default is 1 day.
    private static final long MAX_AGE = 24 * 60 * 60;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 1 Set the allowed origin addresses
        corsConfiguration.addAllowedHeader("*"); // 2 Set the allowed request headers
        corsConfiguration.addAllowedMethod("*"); // 3 Set the allowed request methods
        corsConfiguration.setMaxAge(MAX_AGE);
        source.registerCorsConfiguration("/**", corsConfiguration); // 4 Configure cross-origin settings for all endpoints
        return new CorsFilter(source);
    }
}
