package com.mangofactory.swagger.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;

public class DefaultSwaggerPathProvider implements SwaggerPathProvider {
   @Autowired
   private ServletContext servletContext;
   private String apiResourceSuffix = "";

   public String getApiResourcePrefix() {
      return apiResourceSuffix;
   }

   public String getAppBasePath() {
      return UriComponentsBuilder
            .fromHttpUrl("http://127.0.0.1:8080")
            .path(servletContext.getContextPath())
            .build()
            .toString();
   }

   @Override
   public String getSwaggerDocumentationBasePath() {
      return UriComponentsBuilder
            .fromHttpUrl(getAppBasePath())
            .pathSegment("api-docs/")
            .build()
            .toString();
   }

   @Override
   public String getRequestMappingEndpoint(String requestMappingPattern) {
      String result = requestMappingPattern;
      //remove regex portion '/{businessId:\\w+}'
      result = result.replaceAll("\\{(.*?):.*?\\}", "{$1}");
      return result.isEmpty() ? "/" : result;
   }

   public String getApiResourceSuffix() {
      return apiResourceSuffix;
   }

   public void setApiResourceSuffix(String apiResourceSuffix) {
      this.apiResourceSuffix = apiResourceSuffix;
   }
}
