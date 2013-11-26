package com.mangofactory.swagger.readers

import com.mangofactory.swagger.core.DefaultControllerResourceGroupingStrategy
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.model.ApiDescription
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

import static com.mangofactory.swagger.ScalaUtils.fromOption

@Mixin(RequestMappingSupport)
class ApiDescriptionReaderSpec extends Specification {

   def "should generate an api description for each request mapping pattern"() {
    given:
      ApiDescriptionReader apiDescriptionReader = new ApiDescriptionReader(new DefaultControllerResourceGroupingStrategy())
      RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
              [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}', '/somePath/{businessId:\\d+}')]
      )

      HandlerMethod handlerMethod = dummyHandlerMethod()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo, handlerMethod)
    when:
      apiDescriptionReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      def descriptionList = result['apiDescriptionList']
      descriptionList.size == 2

      ApiDescription apiDescription = descriptionList[0]
      ApiDescription secondApiDescription = descriptionList[1]

      apiDescription.path() == '/somePath/(businessId)'
      fromOption(apiDescription.description()) == dummyHandlerMethod().method.name

      secondApiDescription.path() == '/somePath/(businessId)'
      fromOption(secondApiDescription.description()) == dummyHandlerMethod().method.name
   }

}
