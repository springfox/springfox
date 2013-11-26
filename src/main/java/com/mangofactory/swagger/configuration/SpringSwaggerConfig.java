package com.mangofactory.swagger.configuration;


import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SpringSwaggerConfig {

   @Autowired
   @Getter
   private List<RequestMappingHandlerMapping> handlerMappings;

   @Bean public List<RequestMappingHandlerMapping> swaggerRequestMappingHandlerMappings(){
      return handlerMappings;
   }

   @Bean
   public ControllerResourceNamingStrategy defaultControllerResourceNamingStrategy() {
      return new DefaultControllerResourceNamingStrategy();
   }

   @Bean
   public List<Class<? extends Annotation>> defaultExcludeAnnotations() {
      List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();
      annotations.add(ApiIgnore.class);
      return annotations;
   }

   @Bean
   public SwaggerPathProvider defaultSwaggerPathProvider() {
      DefaultSwaggerPathProvider swaggerPathProvider = new DefaultSwaggerPathProvider();
      return  swaggerPathProvider;
   }

   @Bean
   public SwaggerCache swaggerCache(){
      return new SwaggerCache();
   }
}
