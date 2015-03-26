package springfox.documentation.swagger.web

import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.service.ApiDescription
import springfox.documentation.spring.web.scanners.ApiDescriptionReader
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.ApiOperationReader
import springfox.documentation.swagger.mixins.SwaggerPathProviderSupport

import javax.servlet.ServletContext

import static springfox.documentation.spring.web.Paths.*

@Mixin([RequestMappingSupport, SwaggerPathProviderSupport])
class SwaggerApiDescriptionReaderSpec extends DocumentationContextSpec {

   def "should generate an api description for each request mapping pattern"() {
      given:
        def operationReader = Mock(ApiOperationReader)
        ApiDescriptionReader sut = new ApiDescriptionReader(operationReader)
      and:
        plugin.pathProvider(pathProvider)
        RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
                [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}', '/somePath/{businessId:\\d+}')]
        )
        RequestMappingContext mappingContext = new RequestMappingContext(context(), requestMappingInfo,
                dummyHandlerMethod())
        operationReader.read(_) >> []
      when:
        def descriptionList = sut.read(mappingContext)

      then:
        descriptionList.size() == 2

        ApiDescription apiDescription = descriptionList[0]
        ApiDescription secondApiDescription = descriptionList[1]

        apiDescription.getPath() == '/somePath/{businessId}'
        apiDescription.getDescription() == dummyHandlerMethod().method.name

        secondApiDescription.getPath() == '/somePath/{businessId}'
        secondApiDescription.getDescription() == dummyHandlerMethod().method.name

      where:
        pathProvider                                      | prefix
        absoluteSwaggerPathProvider()                     | "/api/v1"
        relativeSwaggerPathProvider(Mock(ServletContext)) | ""
   }

   def "should sanitize request mapping endpoints"() {
      expect:
        sanitizeRequestMappingPattern(mappingPattern) == expected

      where:
        mappingPattern             | expected
        ""                         | "/"
        "/"                        | "/"
        "/businesses"              | "/businesses"
        "/{businessId:\\w+}"       | "/{businessId}"
        "/businesses/{businessId}" | "/businesses/{businessId}"
        "/foo/bar:{baz}"           | "/foo/bar:{baz}"
        "/foo:{foo}/bar:{baz}"     | "/foo:{foo}/bar:{baz}"
        "/foo/bar:{baz:\\w+}"      | "/foo/bar:{baz}"

   }
}
