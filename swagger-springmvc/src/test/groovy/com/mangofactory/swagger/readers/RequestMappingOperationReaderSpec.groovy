package com.mangofactory.swagger.readers

import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SwaggerAddressProviderSupport
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.mangofactory.swagger.scanners.ResourceGroup
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import spock.lang.Unroll

@Mixin([RequestMappingSupport, SwaggerAddressProviderSupport])
class RequestMappingOperationReaderSpec extends Specification {

  @Unroll
  def "should generate an api paths for each request mapping pattern"() {
    given:
      RequestMappingOperationReader apiPathReader = new RequestMappingOperationReader(addressProvider, [])
      RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
              [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}',
                      '/somePath/another/{businessId:\\d+}')]
      )

      HandlerMethod handlerMethod = dummyHandlerMethod()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo, handlerMethod)

      def settings = new SwaggerGlobalSettings()
      SwaggerModelsConfiguration springSwaggerConfig = new SwaggerModelsConfiguration()
      settings.alternateTypeProvider = springSwaggerConfig.alternateTypeProvider();
      context.put("swaggerGlobalSettings", settings)
      context.put("currentResourceGroup", Mock(ResourceGroup))
    when:
      apiPathReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      Map apiPathMap = result['requestMappingOperations']
      apiPathMap.size() == 2

      apiPathMap["/somePath/{businessId}"]
      println apiPathMap["/somePath/{businessId}"]
      apiPathMap["/somePath/another/{businessId}"]

    and:
      RequestMethod[] methods = RequestMethod.values()
      apiPathMap.each { key, val ->
        methods.each {
          assert val[it]
        }
      }

    where:
      addressProvider << [
              absoluteSwaggerAddressProvider(),
              relativeSwaggerAddressProvider()
      ]
  }

  @Unroll
  def "should sanitize RequestMappingPattern [#mappingPattern] to [#expected]"() {
    expect:
      new RequestMappingOperationReader(absoluteSwaggerAddressProvider(), []).sanitizeRequestMappingPattern(mappingPattern) ==
              expected

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
