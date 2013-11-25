package com.mangofactory.swagger.readers

import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class ApiOperationReaderSpec extends Specification{

   def "Should generate an operation for each http method supported by the request mapping"() {

    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
              [patternsRequestCondition: patternsRequestCondition('/doesNotMatterForThisTest', '/somePath/{businessId:\\d+}')]
      )

      HandlerMethod handlerMethod = dummyHandlerMethod()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo, handlerMethod)

      ApiOperationReader apiOperationReader = new ApiOperationReader()

    when:
      apiOperationReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['operations'].size == RequestMethod.values().size()
      result['operations'][0].method == RequestMethod.values()[0].toString()
   }

}
