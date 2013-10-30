package com.mangofactory.swagger.configuration;


import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.model.ApiKey;
import com.wordnik.swagger.model.AuthorizationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SpringSwaggerConfig {
   @Bean
   @Scope("prototype")
   @Autowired
   public SwaggerApi swaggerConfiguration(WebApplicationContext webApplicationContext,
       SwaggerConfig defaultSwaggerConfig) {
      return null;
   }

   @Bean
   @Autowired
   public SwaggerConfig defaultSwaggerConfig() {
      return null;
   }

   @Bean
   @Autowired
   public List<AuthorizationType> defaultAuthorizations() {
      AuthorizationType authorizationType = new ApiKey("apiKey", "header");
      return new ArrayList<>(Arrays.asList(authorizationType));
   }
}
