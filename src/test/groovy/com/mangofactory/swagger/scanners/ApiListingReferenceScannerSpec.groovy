package com.mangofactory.swagger.scanners

import com.mangofactory.swagger.annotations.ApiIgnore
import com.mangofactory.swagger.core.DefaultControllerResourceNamingStrategy
import com.mangofactory.swagger.mixins.AccessorAssertions
import com.mangofactory.swagger.mixins.RequestMappingSupport
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import spock.lang.Specification
import spock.lang.Unroll

@Mixin([AccessorAssertions, RequestMappingSupport])
class ApiListingReferenceScannerSpec extends Specification {

   def "accessor assertions"() {
    when:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
    then:
      assertAccessor(apiListingReferenceScanner, method, value)
    where:
      method                         | value
      'resourceGroup'                | 's'
      'controllerNamingStrategy'     | new DefaultControllerResourceNamingStrategy()
      'requestMappingHandlerMapping' | []
      'excludeAnnotations'           | []
   }

   def "should not get expected exceptions with invalid constructor params"() {
    when:
      new ApiListingReferenceScanner(handlerMappings, controllerNamingStrategy)

    then:
      def exception = thrown(IllegalArgumentException)
      exception.message == message

    where:
      handlerMappings              | controllerNamingStrategy | message
      null                         | null                     | "No RequestMappingHandlerMapping's found have you added <mvc:annotation-driven/>"
      []                           | null                     | "No RequestMappingHandlerMapping's found have you added <mvc:annotation-driven/>"
      [requestMappingInfo("path")] | null                     | "controllerNamingStrategy is required"
   }

   def "ignore requestMappings "() {
    given:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
      apiListingReferenceScanner.setExcludeAnnotations([ApiIgnore])
      def ignorableMethod = ignorableHandlerMethod()

    expect:
      true == apiListingReferenceScanner.doesNotHaveIgnoredAnnotatedRequestMapping(ignorableMethod)
      false == apiListingReferenceScanner.shouldIncludeRequestMapping(requestMappingInfo("p"), ignorableMethod)
   }

   @Unroll("Pattern : #patterns | path: #path")
   def "Should only include matching controller url patterns"() {
    when:
      RequestMappingHandlerMapping requestMappingHandlerMapping = Mock()
      RequestMappingInfo requestMappingInfo = requestMappingInfo(path)
      requestMappingHandlerMapping.getHandlerMethods() >> [(requestMappingInfo): dummyHandlerMethod()]

      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner([requestMappingHandlerMapping], new DefaultControllerResourceNamingStrategy())
      apiListingReferenceScanner.setIncludePatterns(patterns)
      apiListingReferenceScanner.scan()

    then:
      shouldMatch == (null != apiListingReferenceScanner.apiListingReferences.find { it.path() == "/default/$group" })

    where:
      patterns                 | path                           | group        | shouldMatch
      ['']                     | 'path'                         | 'path'       | false
      ['sdfs']                 | 'path'                         | 'path'       | false
      ['/path']                | 'path'                         | 'path'       | true
      ['/?ath']                | 'path'                         | 'path'       | true
      ['/*ath']                | 'path'                         | 'path'       | true
      ['/**path']              | 'path'                         | 'path'       | true
      ['/path/']               | 'path'                         | 'path'       | false
      ['/**path']              | 'path'                         | 'path'       | true
      ['/**']                  | 'path'                         | 'path'       | true
      ['/businesses/accounts'] | '/businesses/accounts'         | 'businesses' | true
      ['/**']                  | '/businesses/accounts'         | 'businesses' | true
      ['/*/accounts']          | '/businesses/accounts'         | 'businesses' | true
      ['/*/accounts']          | '/businesses/accounts'         | 'businesses' | true
      ['/**/accounts']         | '/businesses/accounts'         | 'businesses' | true
      ['/**businesses/**']     | '/businesses/accounts'         | 'businesses' | true
      ['/**']                  | '/businesses/accounts/balance' | 'businesses' | true
      ['/path', '/?ath']       | 'path'                         | 'path'       | true
      ['/path', '/notAMatch']  | 'path'                         | 'path'       | true
      ['/both', '/doNotMatch'] | 'path'                         | 'path'       | false
   }

}
