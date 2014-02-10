package com.mangofactory.swagger.scanners
import com.mangofactory.swagger.annotations.ApiIgnore
import com.mangofactory.swagger.core.DefaultControllerResourceNamingStrategy
import com.mangofactory.swagger.core.DefaultSwaggerPathProvider
import com.mangofactory.swagger.mixins.AccessorAssertions
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.wordnik.swagger.model.ApiListingReference
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import spock.lang.Specification

import static com.mangofactory.swagger.ScalaUtils.fromOption

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

   def "Should only include matching controller url patterns"() {
    when:
      RequestMappingHandlerMapping requestMappingHandlerMapping = Mock()
      RequestMappingInfo businessRequestMappingInfo = requestMappingInfo("/businesses")
      RequestMappingInfo accountsRequestMappingInfo = requestMappingInfo("/accounts")

      requestMappingHandlerMapping.getHandlerMethods() >>
              [(businessRequestMappingInfo): dummyHandlerMethod(), (accountsRequestMappingInfo): dummyHandlerMethod()]

      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
      apiListingReferenceScanner.setControllerNamingStrategy(new DefaultControllerResourceNamingStrategy())
      apiListingReferenceScanner.setRequestMappingHandlerMapping([requestMappingHandlerMapping])
      apiListingReferenceScanner.setIncludePatterns([".*"])
      apiListingReferenceScanner.setRequestMappingPatternMatcher(new RegexRequestMappingPatternMatcher())
      apiListingReferenceScanner.setSwaggerGroup("someGroup")
      apiListingReferenceScanner.setSwaggerPathProvider(new DefaultSwaggerPathProvider(servletContext: servletContext()))
      List<ApiListingReference> apiListingReferences = apiListingReferenceScanner.scan()

    then:
      apiListingReferences.size == 2
      ApiListingReference businessListingReference = apiListingReferences.find({fromOption(it.description()) == "businesses"})
      businessListingReference.path() == 'http://127.0.0.1:8080/context-path/api-docs/someGroup/businesses'
      businessListingReference.position() > -1

      ApiListingReference accountsListingReference = apiListingReferences.find({fromOption(it.description()) == "accounts"})
      accountsListingReference.path() == 'http://127.0.0.1:8080/context-path/api-docs/someGroup/accounts'
      businessListingReference.position() > -1

   }

   def "should group controller paths"(){
    when:
      RequestMappingHandlerMapping requestMappingHandlerMapping = Mock()
      RequestMappingInfo businessRequestMappingInfo = requestMappingInfo("/api/v1/businesses")
      RequestMappingInfo accountsRequestMappingInfo = requestMappingInfo("/api/v1/accounts")

      requestMappingHandlerMapping.getHandlerMethods() >>
              [
                (businessRequestMappingInfo): dummyHandlerMethod(),
                (accountsRequestMappingInfo): dummyHandlerMethod()
              ]

      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()

      DefaultControllerResourceNamingStrategy defaultControllerResourceNamingStrategy = new DefaultControllerResourceNamingStrategy()
      defaultControllerResourceNamingStrategy.setSkipPathCount(2)

      apiListingReferenceScanner.setControllerNamingStrategy(defaultControllerResourceNamingStrategy)
      apiListingReferenceScanner.setRequestMappingHandlerMapping([requestMappingHandlerMapping])
      apiListingReferenceScanner.setIncludePatterns([".*"])
      apiListingReferenceScanner.setRequestMappingPatternMatcher(new RegexRequestMappingPatternMatcher())
      apiListingReferenceScanner.setSwaggerGroup("someGroup")
      apiListingReferenceScanner.setSwaggerPathProvider(new DefaultSwaggerPathProvider(servletContext: servletContext()))
      List<ApiListingReference> apiListingReferences = apiListingReferenceScanner.scan()

    then:
      apiListingReferences.size == 2
      ApiListingReference businessListingReference = apiListingReferences.find({fromOption(it.description()) == "businesses"})
      businessListingReference.path() == 'http://127.0.0.1:8080/context-path/api-docs/someGroup/businesses'
      ApiListingReference accountsListingReference = apiListingReferences.find({fromOption(it.description()) == "accounts"})
      accountsListingReference.path() == 'http://127.0.0.1:8080/context-path/api-docs/someGroup/accounts'
   }
}