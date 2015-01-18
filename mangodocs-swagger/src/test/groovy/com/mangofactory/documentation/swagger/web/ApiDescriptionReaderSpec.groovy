package com.mangofactory.documentation.swagger.web

import com.mangofactory.documentation.service.model.ApiDescription
import com.mangofactory.documentation.spi.service.contexts.RequestMappingContext
import com.mangofactory.documentation.spring.web.scanners.ApiDescriptionReader
import com.mangofactory.documentation.spring.web.readers.operation.ApiOperationReader
import com.mangofactory.documentation.swagger.mixins.SwaggerPathProviderSupport
import com.mangofactory.documentation.spring.web.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import org.springframework.web.servlet.mvc.method.RequestMappingInfo

import static com.mangofactory.documentation.spring.web.scanners.ApiDescriptionReader.sanitizeRequestMappingPattern


@Mixin([RequestMappingSupport, SwaggerPathProviderSupport])
class ApiDescriptionReaderSpec extends DocumentationContextSpec {
  ApiDescriptionReader sut = new ApiDescriptionReader(Mock(ApiOperationReader))

   def "should generate an api description for each request mapping pattern"() {
      given:
        plugin.pathProvider(pathProvider)
        RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
                [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}', '/somePath/{businessId:\\d+}')]
        )
        RequestMappingContext mappingContext = new RequestMappingContext(context(), requestMappingInfo,
                dummyHandlerMethod())

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
