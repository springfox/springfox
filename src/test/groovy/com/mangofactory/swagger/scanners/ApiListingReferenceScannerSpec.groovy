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
      'swaggerGroup'                 | 's'
      'controllerNamingStrategy'     | new DefaultControllerResourceNamingStrategy()
      'requestMappingHandlerMapping' | []
      'excludeAnnotations'           | []
   }

   @Unroll
   def "should not get expected exceptions with invalid constructor params"() {
    when:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
      apiListingReferenceScanner.requestMappingHandlerMapping = handlerMappings
      apiListingReferenceScanner.controllerNamingStrategy = controllerNamingStrategy
      apiListingReferenceScanner.swaggerGroup = swaggerGroup
      apiListingReferenceScanner.scan()

    then:
      def exception = thrown(IllegalArgumentException)
      exception.message == message

    where:
      handlerMappings              | controllerNamingStrategy                      | swaggerGroup | message
      null                         | null                                          | null         | "No RequestMappingHandlerMapping's found have you added <mvc:annotation-driven/>"
      []                           | null                                          | null         | "No RequestMappingHandlerMapping's found have you added <mvc:annotation-driven/>"
      [requestMappingInfo("path")] | null                                          | null         | "controllerNamingStrategy is required"
      [requestMappingInfo("path")] | new DefaultControllerResourceNamingStrategy() | null         | "swaggerGroup is required"
      [requestMappingInfo("path")] | new DefaultControllerResourceNamingStrategy() | ""           | "swaggerGroup must not be empty"
   }

   def "ignore requestMappings "() {
    given:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
      apiListingReferenceScanner.setExcludeAnnotations([ApiIgnore])
      def ignorableMethod = ignorableHandlerMethod()

    expect:
      true == apiListingReferenceScanner.hasIgnoredAnnotatedRequestMapping(ignorableMethod)
      false == apiListingReferenceScanner.shouldIncludeRequestMapping(requestMappingInfo("p"), ignorableMethod)
   }

   def "include requestMapping"() {
    given:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
      apiListingReferenceScanner.setIncludePatterns([patterns])
      apiListingReferenceScanner.setExcludeAnnotations([ApiIgnore])

    expect:
      sholdInclude == apiListingReferenceScanner.shouldIncludeRequestMapping(requestMapping, handlerMethod)

    where:
      handlerMethod            | requestMapping                   | patterns    | sholdInclude
      dummyHandlerMethod()     | requestMappingInfo("/some-path") | '.*'        | true
      ignorableHandlerMethod() | requestMappingInfo("/some-path") | '.*'        | false
      dummyHandlerMethod()     | requestMappingInfo("/some-path") | '/no-match' | false
      ignorableHandlerMethod() | requestMappingInfo("/some-path") | '/no-match' | false

   }

   @Unroll("Pattern : #patterns | path: #path")
   def "Should only include matching controller url patterns"() {
    when:
      RequestMappingHandlerMapping requestMappingHandlerMapping = Mock()
      RequestMappingInfo requestMappingInfo = requestMappingInfo(path)
      requestMappingHandlerMapping.getHandlerMethods() >> [(requestMappingInfo): dummyHandlerMethod()]

      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
      apiListingReferenceScanner.setControllerNamingStrategy(new DefaultControllerResourceNamingStrategy())
      apiListingReferenceScanner.setRequestMappingHandlerMapping([requestMappingHandlerMapping])
      apiListingReferenceScanner.setIncludePatterns(patterns)
      apiListingReferenceScanner.setRequestMappingPatternMatcher(new AntRequestMappingPatternMatcher())
      apiListingReferenceScanner.setSwaggerGroup("someGroup")
      apiListingReferenceScanner.scan()

    then:
      shouldMatch == (null != apiListingReferenceScanner.apiListingReferences.find { it.path() == "/$controllerGroupName" })

    where:
      patterns                 | path                           | controllerGroupName | shouldMatch
      ['']                     | 'path'                         | 'path'              | false
      ['sdfs']                 | 'path'                         | 'path'              | false
      ['/path']                | 'path'                         | 'path'              | true
      ['/?ath']                | 'path'                         | 'path'              | true
      ['/*ath']                | 'path'                         | 'path'              | true
      ['/**path']              | 'path'                         | 'path'              | true
      ['/path/']               | 'path'                         | 'path'              | false
      ['/**path']              | 'path'                         | 'path'              | true
      ['/**']                  | 'path'                         | 'path'              | true
      ['/businesses/accounts'] | '/businesses/accounts'         | 'businesses'        | true
      ['/**']                  | '/businesses/accounts'         | 'businesses'        | true
      ['/*/accounts']          | '/businesses/accounts'         | 'businesses'        | true
      ['/*/accounts']          | '/businesses/accounts'         | 'businesses'        | true
      ['/**/accounts']         | '/businesses/accounts'         | 'businesses'        | true
      ['/**businesses/**']     | '/businesses/accounts'         | 'businesses'        | true
      ['/**']                  | '/businesses/accounts/balance' | 'businesses'        | true
      ['/path', '/?ath']       | 'path'                         | 'path'              | true
      ['/path', '/notAMatch']  | 'path'                         | 'path'              | true
      ['/both', '/doNotMatch'] | 'path'                         | 'path'              | false
   }

}
