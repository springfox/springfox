package com.mangofactory.spring.web.readers
import com.mangofactory.service.model.ApiDescription
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SwaggerPathProviderSupport
import com.mangofactory.spring.web.scanners.RequestMappingContext
import org.springframework.web.servlet.mvc.method.RequestMappingInfo

import static com.mangofactory.spring.web.readers.ApiDescriptionReader.*

@Mixin([RequestMappingSupport, SwaggerPathProviderSupport])
class ApiDescriptionReaderSpec extends DocumentationContextSpec {
  ApiDescriptionReader sut = new ApiDescriptionReader(Mock(ApiOperationReader))

   def "should generate an api description for each request mapping pattern"() {
      given:
        def context = plugin.pathProvider(pathProvider).build(contextBuilder)
        RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
                [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}', '/somePath/{businessId:\\d+}')]
        )
        RequestMappingContext mappingContext = new RequestMappingContext(context, requestMappingInfo, dummyHandlerMethod())

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
