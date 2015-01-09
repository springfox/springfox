package com.mangofactory.swagger.configuration;

import com.mangofactory.swagger.annotations.EnableSwagger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSwagger
@ComponentScan("com.mangofactory.swagger.dummy")
public class MultipleRMHAConfig {


}
