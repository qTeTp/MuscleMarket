package com.example.muscle_market.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filterRegBean =
                new FilterRegistrationBean<>(new ForwardedHeaderFilter());
        filterRegBean.setOrder(0); // 가장 먼저 필터 적용
        return filterRegBean;
    }
}
