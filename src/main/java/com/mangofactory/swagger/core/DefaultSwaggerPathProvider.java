package com.mangofactory.swagger.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;

public class DefaultSwaggerPathProvider implements SwaggerPathProvider {
   @Autowired
   private ServletContext servletContext;

   @Setter
   @Getter
   private String apiResourceSuffix = "";

   public String getApiResourcePrefix() {
      return apiResourceSuffix;
   }

   public String getContextPath() {
      return servletContext.getContextPath();
   }

   public String getAppBasePath() {
      return "http://127.0.0.1:8080" + getContextPath();
   }
}
