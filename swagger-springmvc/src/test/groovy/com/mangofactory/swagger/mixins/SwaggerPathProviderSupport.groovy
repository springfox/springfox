package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.web.AbsolutePathProvider
import com.mangofactory.spring.web.RelativePathProvider
import com.mangofactory.spring.web.PathProvider

import javax.servlet.ServletContext

class SwaggerPathProviderSupport {
  AbsolutePathProvider absoluteSwaggerPathProvider(){
      PathProvider swaggerPathProvider = new AbsolutePathProvider();
      swaggerPathProvider.setApiResourcePrefix("api/v1");
      swaggerPathProvider.servletContext = [getContextPath: {return "/context-path"}] as ServletContext
      return swaggerPathProvider
   }

  RelativePathProvider relativeSwaggerPathProvider(){
      new RelativePathProvider()
   }
}
