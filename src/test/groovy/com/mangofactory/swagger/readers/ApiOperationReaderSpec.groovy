package com.mangofactory.swagger.readers

import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

import static org.springframework.web.bind.annotation.RequestMethod.POST

@Mixin(RequestMappingSupport)
class ApiOperationReaderSpec extends Specification{

   def "Should generate default operation on handler method without swagger annotations"() {

    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
              [
                      patternsRequestCondition: patternsRequestCondition('/doesNotMatterForThisTest', '/somePath/{businessId:\\d+}'),
                      requestMethodsRequestCondition : requestMethodsRequestCondition(RequestMethod.PATCH, POST)

              ]
      )

      HandlerMethod handlerMethod = dummyHandlerMethod()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo, handlerMethod)

      ApiOperationReader apiOperationReader = new ApiOperationReader()

    when:
      apiOperationReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      def apiOperation = result['operations'][0]
      apiOperation.method == RequestMethod.PATCH.toString()
      apiOperation.summary == handlerMethod.method.name
      apiOperation.notes == handlerMethod.method.name
      apiOperation.nickname == handlerMethod.method.name
      apiOperation.position == 0

      def secondApiOperation = result['operations'][1]
      secondApiOperation.position == 1
   }

}
