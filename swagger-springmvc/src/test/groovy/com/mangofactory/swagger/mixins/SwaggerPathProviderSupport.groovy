package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.paths.AbsoluteSwaggerPathProvider
import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider
import com.mangofactory.swagger.paths.SwaggerPathProvider

import javax.servlet.ServletContext

class SwaggerPathProviderSupport {
   def absoluteSwaggerPathProvider(){
      SwaggerPathProvider swaggerPathProvider = new AbsoluteSwaggerPathProvider();
      swaggerPathProvider.setApiResourcePrefix("api/v1");
      swaggerPathProvider.servletContext = [getContextPath: {return "/context-path"}] as ServletContext
      return swaggerPathProvider
   }

   def relativeSwaggerPathProvider(){
      new RelativeSwaggerPathProvider()
   }
}
