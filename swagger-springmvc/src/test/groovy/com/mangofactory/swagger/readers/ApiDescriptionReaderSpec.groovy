package com.mangofactory.swagger.readers
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.annotations.ApiIgnore
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.core.RequestMappingEvaluator
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SwaggerPathProviderSupport
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.scanners.RegexRequestMappingPatternMatcher
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.mangofactory.swagger.models.dto.ApiDescription
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList

@Mixin([RequestMappingSupport, SwaggerPathProviderSupport])
class ApiDescriptionReaderSpec extends Specification {

   def "should generate an api description for each request mapping pattern"() {
      given:
        RequestMappingEvaluator evaluator = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
                RegexRequestMappingPatternMatcher(), newArrayList(".*?"))
        ApiDescriptionReader apiDescriptionReader = new ApiDescriptionReader(pathProvider, [], evaluator)
        RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
                [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}', '/somePath/{businessId:\\d+}')]
        )

        HandlerMethod handlerMethod = dummyHandlerMethod()
        RequestMappingContext context = new RequestMappingContext(requestMappingInfo, handlerMethod)

        def settings = new SwaggerGlobalSettings()
        SwaggerModelsConfiguration springSwaggerConfig = new SwaggerModelsConfiguration()
        settings.alternateTypeProvider = springSwaggerConfig.alternateTypeProvider(new TypeResolver());
        context.put("swaggerGlobalSettings", settings)
      when:
        apiDescriptionReader.execute(context)
        Map<String, Object> result = context.getResult()

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
        RequestMappingEvaluator evaluator = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
                RegexRequestMappingPatternMatcher(), newArrayList())
        new ApiDescriptionReader(absoluteSwaggerPathProvider(), [], evaluator)
                .sanitizeRequestMappingPattern(mappingPattern) == expected

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
