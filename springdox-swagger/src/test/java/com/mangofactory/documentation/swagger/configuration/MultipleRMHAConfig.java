package com.mangofactory.documentation.swagger.configuration;

import com.mangofactory.documentation.swagger.annotations.EnableSwagger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSwagger
@ComponentScan("com.mangofactory.documentation.spring.web.dummy")
public class MultipleRMHAConfig {


}
