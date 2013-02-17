package com.mangofactory.swagger.spring.test;

import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.SwaggerConfigurationExtension;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan("com.mangofactory.swagger.spring.test")
public class TestConfiguration {

    @Bean
    public DocumentationController documentationController() {
        return new DocumentationController();
    }

    @Bean
    public SwaggerConfiguration swaggerConfiguration() {
        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();
        swaggerConfiguration.setApiVersion("2.0");
        swaggerConfiguration.setBasePath("/some-path");
        return swaggerConfiguration;
    }

    @Bean
    public SwaggerConfigurationExtension swaggerConfigurationExtension() {
        return new SwaggerConfigurationExtension();
    }

}
