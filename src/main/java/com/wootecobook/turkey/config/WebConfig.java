package com.wootecobook.turkey.config;

import com.wootecobook.turkey.commons.converter.ImageTypeConverter;
import com.wootecobook.turkey.commons.interceptor.LoginInterceptor;
import com.wootecobook.turkey.commons.resolver.UserSessionArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableJpaAuditing
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserSessionArgumentResolver());
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/users/form", "/login", "/api/users")
                .excludePathPatterns("/docs/api-guide.html")
                .excludePathPatterns("/css/**", "/js/**", "/images/**");
    }

    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addConverter(new ImageTypeConverter());
    }
}
