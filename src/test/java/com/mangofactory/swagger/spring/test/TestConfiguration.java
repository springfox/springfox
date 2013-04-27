package com.mangofactory.swagger.spring.test;

import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.configuration.DefaultConfigurationModule;
import com.mangofactory.swagger.configuration.DocumentationConfig;
import com.mangofactory.swagger.configuration.ExtensibilityModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@Import(DocumentationConfig.class)
@ComponentScan("com.mangofactory.swagger.spring.test")
public class TestConfiguration {

    @Bean
    public SwaggerConfiguration swaggerConfiguration(DefaultConfigurationModule defaultConfig,
                                                     ExtensibilityModule extensibility) {
        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration("2.0", "/some-path");
        swaggerConfiguration.getExcludedResources().add("/excluded");
        return extensibility.apply(defaultConfig.apply(swaggerConfiguration));
    }


}
