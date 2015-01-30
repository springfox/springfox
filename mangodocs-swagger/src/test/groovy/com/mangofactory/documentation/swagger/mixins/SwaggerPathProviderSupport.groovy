package com.mangofactory.documentation.swagger.mixins

import com.mangofactory.documentation.swagger.web.AbsolutePathProvider
import com.mangofactory.documentation.spring.web.RelativePathProvider
import com.mangofactory.documentation.service.PathProvider

import javax.servlet.ServletContext

class SwaggerPathProviderSupport {
  AbsolutePathProvider absoluteSwaggerPathProvider(){
      PathProvider swaggerPathProvider = new AbsolutePathProvider();
      swaggerPathProvider.setApiResourcePrefix("api/v1");
      swaggerPathProvider.servletContext = [getContextPath: {return "/context-path"}] as ServletContext
      return swaggerPathProvider
   }

  RelativePathProvider relativeSwaggerPathProvider(ServletContext servletContext){
      new RelativePathProvider(servletContext)
   }
}
