package com.mangofactory.swagger.readers
import com.mangofactory.service.model.ApiDescription
import com.mangofactory.springmvc.plugin.DocumentationContextBuilder
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.mixins.SwaggerPathProviderSupport
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

import javax.servlet.ServletContext

import static com.mangofactory.swagger.readers.ApiDescriptionReader.*

@Mixin([RequestMappingSupport, SwaggerPathProviderSupport, DocumentationContextSupport, SpringSwaggerConfigSupport])
class ApiDescriptionReaderSpec extends Specification {
  DocumentationContextBuilder contextBuilder
  SwaggerSpringMvcPlugin plugin
  ApiOperationReader operationReader
  ApiDescriptionReader sut

  def setup() {
    contextBuilder = defaultContextBuilder(defaults(Mock(ServletContext)))
    plugin = new SwaggerSpringMvcPlugin()
    operationReader = Mock(ApiOperationReader)
    sut = new ApiDescriptionReader(operationReader)
  }

   def "should generate an api description for each request mapping pattern"() {
      given:
        def context = plugin.pathProvider(pathProvider).build(contextBuilder)
        RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
                [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}', '/somePath/{businessId:\\d+}')]
        )
        RequestMappingContext mappingContext = new RequestMappingContext(context, requestMappingInfo, dummyHandlerMethod())

      when:
        sut.execute(mappingContext)
        Map<String, Object> result = mappingContext.getResult()

      then:
        def descriptionList = result['apiDescriptionList']
        descriptionList.size == 2

        ApiDescription apiDescription = descriptionList[0]
        ApiDescription secondApiDescription = descriptionList[1]

        apiDescription.getPath() == prefix + '/somePath/{businessId}'
        apiDescription.getDescription() == dummyHandlerMethod().method.name

        secondApiDescription.getPath() == prefix + '/somePath/{businessId}'
        secondApiDescription.getDescription() == dummyHandlerMethod().method.name

      where:
        pathProvider                  | prefix
        absoluteSwaggerPathProvider() | "/api/v1"
        relativeSwaggerPathProvider() | ""
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
