package springfox.documentation.swagger.mixins

import springfox.documentation.spring.web.AbstractPathProvider
import springfox.documentation.spring.web.RelativePathProvider
import springfox.documentation.swagger.web.AbsolutePathProvider

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
