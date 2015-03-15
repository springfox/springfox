package springdox.documentation.spring.web.scanners

import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springdox.documentation.service.ApiDescription
import springdox.documentation.spi.service.contexts.RequestMappingContext
import springdox.documentation.spring.web.Paths
import springdox.documentation.spring.web.RelativePathProvider
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec
import springdox.documentation.spring.web.readers.operation.ApiOperationReader

import javax.servlet.ServletContext

@Mixin([RequestMappingSupport])
class ApiDescriptionReaderSpec extends DocumentationContextSpec {

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

        apiDescription.getPath() == prefix + '/somePath/{businessId}'
        apiDescription.getDescription() == dummyHandlerMethod().method.name

        secondApiDescription.getPath() == prefix + '/somePath/{businessId}'
        secondApiDescription.getDescription() == dummyHandlerMethod().method.name

      where:
        pathProvider                                    | prefix
        new RelativePathProvider(Mock(ServletContext))  | ""
   }

   def "should sanitize request mapping endpoints"() {
      expect:
        Paths.sanitizeRequestMappingPattern(mappingPattern) == expected

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
