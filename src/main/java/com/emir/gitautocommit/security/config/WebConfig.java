package com.emir.gitautocommit.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/dashboard").setViewName("forward:dashboard/index.html");
        registry.addViewController("/dashboard/").setViewName("forward:dashboard/index.html");
        registry.addViewController("/login").setViewName("forward:index.html");
        registry.addViewController("/login/").setViewName("forward:index.html");
    }
}