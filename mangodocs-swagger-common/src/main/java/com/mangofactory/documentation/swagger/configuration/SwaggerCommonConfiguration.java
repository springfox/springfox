package com.mangofactory.documentation.swagger.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
   "com.mangofactory.documentation.swagger.schema",
   "com.mangofactory.documentation.swagger.readers",
   "com.mangofactory.documentation.swagger.web"
})
public class SwaggerCommonConfiguration {
}
