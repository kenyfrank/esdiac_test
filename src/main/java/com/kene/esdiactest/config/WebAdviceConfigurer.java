package com.kene.esdiactest.config;

import com.kene.esdiactest.config.interceptor.RemoteAddressConstraintHandlerInterceptor;
import com.kene.esdiactest.service.RequestPrincipal;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebAdviceConfigurer implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RemoteAddressConstraintHandlerInterceptor(applicationContext));
    }

    @Bean
    public FactoryBean<RequestPrincipal> requestPrincipalFactoryBean() {
        return RemoteAddressConstraintHandlerInterceptor.requestPrincipal();
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");
    }

}
