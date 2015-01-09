package com.mangofactory.swagger.configuration;

import com.mangofactory.swagger.annotations.EnableSwagger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("com.mangofactory.swagger.dummy")
public class DefaultJavaPluginConfig {

}
