package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.core.AbsoluteSwaggerPathProvider
import com.mangofactory.swagger.core.SwaggerPathProvider

import javax.servlet.ServletContext

class SwaggerPathProviderSupport {
   def absoluteSwaggerPathProvider(){
      SwaggerPathProvider swaggerPathProvider = new AbsoluteSwaggerPathProvider();
      swaggerPathProvider.setApiResourceSuffix("/api/v1");
      swaggerPathProvider.servletContext = [getContextPath: {return "/context-path"}] as ServletContext
      return swaggerPathProvider
   }
}
