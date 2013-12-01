package com.mangofactory.swagger.configuration;


import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

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
      return new DefaultSwaggerPathProvider();
   }

   @Bean
   public SwaggerCache swaggerCache(){
      return new SwaggerCache();
   }

   @Bean
   public Set<Class> defaultIgnorableParameterTypes(){
      HashSet<Class> ignored = newHashSet();
      ignored.add(ServletRequest.class);
      ignored.add(ServletResponse.class);
      ignored.add(HttpServletRequest.class);
      ignored.add(HttpServletResponse.class);
      ignored.add(BindingResult.class);
      ignored.add(ServletContext.class);
      return ignored;
   }
}
