package springdox.documentation.swagger.mixins

import springdox.documentation.spring.web.AbstractPathProvider
import springdox.documentation.spring.web.RelativePathProvider
import springdox.documentation.swagger.web.AbsolutePathProvider

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
