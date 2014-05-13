package com.mangofactory.swagger.configuration;

import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("com.mangofactory.swagger.dummy") //Scan some controllers
public class CustomJavaPluginConfig {

   private SpringSwaggerConfig springSwaggerConfig;

   @Autowired
   public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
      this.springSwaggerConfig = springSwaggerConfig;
   }

   @Bean
   public SwaggerSpringMvcPlugin customImplementation(){
      return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
              .swaggerGroup("customPlugin")
              .includePatterns(".*pet.*");
   }

   @Bean
   public SwaggerSpringMvcPlugin secondCustomImplementation(){
      return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
              .swaggerGroup("secondCustomPlugin")
              .includePatterns("/feature.*");
   }
}
