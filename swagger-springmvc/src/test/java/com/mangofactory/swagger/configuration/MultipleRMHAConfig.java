package com.mangofactory.swagger.configuration;

import com.mangofactory.swagger.plugin.EnableSwagger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSwagger
@ComponentScan("com.mangofactory.swagger.dummy")
public class MultipleRMHAConfig {


}
