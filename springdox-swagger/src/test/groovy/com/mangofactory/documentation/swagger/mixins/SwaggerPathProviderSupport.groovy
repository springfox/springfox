package com.mangofactory.documentation.swagger.mixins

import com.mangofactory.documentation.spring.web.AbstractPathProvider
import com.mangofactory.documentation.spring.web.RelativePathProvider
import com.mangofactory.documentation.swagger.web.AbsolutePathProvider

import javax.servlet.ServletContext

@SuppressWarnings("GrMethodMayBeStatic")
class SwaggerPathProviderSupport {
  AbsolutePathProvider absoluteSwaggerPathProvider() {
    def servletContext = [getContextPath: { return "/context-path" }] as ServletContext
    AbstractPathProvider swaggerPathProvider = new AbsolutePathProvider(servletContext);
    return swaggerPathProvider
  }

  RelativePathProvider relativeSwaggerPathProvider(ServletContext servletContext) {
    new RelativePathProvider(servletContext)
  }
}
