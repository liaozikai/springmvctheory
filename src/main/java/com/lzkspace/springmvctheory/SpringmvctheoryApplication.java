package com.lzkspace.springmvctheory;

import com.lzkspace.springmvctheory.servlet.MyDispatcherServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ServletComponentScan
public class SpringmvctheoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringmvctheoryApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean myDispatcherServlet(){
        // 注册ServletRegistrationBean，并实例化MyDispatcherServlet对象
        ServletRegistrationBean registration = new ServletRegistrationBean(new MyDispatcherServlet());
        registration.addUrlMappings("/");
        return registration;

    }
    
}
