package com.mangofactory.swagger.scanners

import com.mangofactory.swagger.annotations.ApiIgnore
import com.mangofactory.swagger.core.ControllerResourceGroupingStrategy
import com.mangofactory.swagger.core.DefaultControllerResourceGroupingStrategy
import com.mangofactory.swagger.mixins.AccessorAssertions
import com.mangofactory.swagger.mixins.RequestMappingSupport
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import spock.lang.Specification

@Mixin([AccessorAssertions, RequestMappingSupport])
class ApiListingReferenceScannerSpec extends Specification {

   def "accessor assertions"() {
    when:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
    then:
      assertAccessor(apiListingReferenceScanner, method, value)
    where:
      method                     | value
      'pathPrefix'               | 's'
      'pathSuffix'               | 'd'
      'controllerNamingStrategy' | new DefaultControllerResourceGroupingStrategy()
      'HandlerMappings'          | []
      'excludeAnnotations'       | []
   }

   def "should not get expected exceptions with invalid constructor params"() {
    when:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner(handlerMappings, controllerNamingStrategy)

    then:
      def exception = thrown(IllegalArgumentException)
      exception.message == message

    where:
      handlerMappings              | controllerNamingStrategy | message
      null                         | null                     | "No RequestMappingHandlerMapping's found have you added <mvc:annotation-driven/>"
      []                           | null                     | "No RequestMappingHandlerMapping's found have you added <mvc:annotation-driven/>"
      [requestMappingInfo("path")] | null                     | "controllerNamingStrategy is required"
   }

//   def "excluded request mappings"(){
//    given:
//      ControllerResourceGroupingStrategy strategy = new DefaultControllerResourceGroupingStrategy()
//      RequestMappingHandlerMapping requestMappingHandlerMapping = Mock()
//      requestMappingHandlerMapping.getHandlerMethods() >> [requestMappingInfo('path', dummyHandlerMethod())]
//      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner([requestMappingHandlerMapping], strategy)
//
//      when:
//      apiListingReferenceScanner.scanForDefaultSpringResources()
//
//      expect:
//
//
//
//   }

   def "ignore requestMappings "() {
    given:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
      apiListingReferenceScanner.setExcludeAnnotations([ApiIgnore.class])
      def ignorableMethod = ignorableHandlerMethod()

    expect:
      true == apiListingReferenceScanner.ignoreAnnotatedRequestMapping(ignorableMethod)
      true == apiListingReferenceScanner.shouldIgnoreRequestMapping(requestMappingInfo("p"), ignorableMethod)
   }


}
