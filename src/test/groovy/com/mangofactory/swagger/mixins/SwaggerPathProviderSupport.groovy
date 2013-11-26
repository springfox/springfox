package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.core.DefaultSwaggerPathProvider

import javax.servlet.ServletContext

class SwaggerPathProviderSupport {
   def swaggerPathProvider(){
      DefaultSwaggerPathProvider swaggerPathProvider = new DefaultSwaggerPathProvider();
      swaggerPathProvider.setApiResourceSuffix("/api/v1/");
      swaggerPathProvider.servletContext = [getContextPath: {return "/context-path"}] as ServletContext
      return swaggerPathProvider
   }
}
