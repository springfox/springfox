package com.mangofactory.swagger.configuration;


import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.ControllerResourceGroupingStrategy;
import com.mangofactory.swagger.core.DefaultControllerResourceGroupingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SpringSwaggerConfig {

   @Bean
   public ControllerResourceGroupingStrategy defaultControllerResourceGroupingStrategy() {
      return new DefaultControllerResourceGroupingStrategy();
   }

   @Bean
   public List<Class<? extends Annotation>> defaultExcludeAnnotations() {
      List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();
      annotations.add(ApiIgnore.class);
      return annotations;
   }
}
