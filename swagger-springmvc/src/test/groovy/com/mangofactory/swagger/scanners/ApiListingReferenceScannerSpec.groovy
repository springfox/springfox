package com.mangofactory.swagger.scanners

import com.mangofactory.swagger.annotations.ApiIgnore
import com.mangofactory.swagger.core.ClassOrApiAnnotationResourceGrouping
import com.mangofactory.swagger.core.RequestMappingEvaluator
import com.mangofactory.swagger.core.ResourceGroupingStrategy
import com.mangofactory.swagger.core.SpringGroupingStrategy
import com.mangofactory.swagger.mixins.AccessorAssertions
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.paths.AbsoluteSwaggerPathProvider
import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider
import com.mangofactory.swagger.models.dto.ApiListingReference
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList

@Mixin([AccessorAssertions, RequestMappingSupport])
class ApiListingReferenceScannerSpec extends Specification {

  def "accessor assertions"() {
    when:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
    then:
      assertAccessor(apiListingReferenceScanner, method, value)
    where:
      method                     | value
      'swaggerGroup'             | 's'
      'resourceGroupingStrategy' | new ClassOrApiAnnotationResourceGrouping()
      'excludeAnnotations'       | []
  }


  def "setter only assertions"() {
    when:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
    then:
      assertSetter(apiListingReferenceScanner, method, value)
    where:
      method                         | value
      'requestMappingHandlerMapping' | []
  }

  def "should not get expected exceptions with invalid constructor params"() {
    when:
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
      apiListingReferenceScanner.requestMappingHandlerMapping = handlerMappings
      apiListingReferenceScanner.resourceGroupingStrategy = resourceGroupingStrategy
      apiListingReferenceScanner.swaggerGroup = swaggerGroup
      apiListingReferenceScanner.requestMappingEvaluator = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
              RegexRequestMappingPatternMatcher(), newArrayList(".*?"))
      apiListingReferenceScanner.scan()

    then:
      def exception = thrown(IllegalArgumentException)
      exception.message == message

    where:
      handlerMappings              | resourceGroupingStrategy                   | swaggerGroup | message
      null                         | null                                       | null         | "No RequestMappingHandlerMapping's found have you added <mvc:annotation-driven/>"
      []                           | null                                       | null         | "No RequestMappingHandlerMapping's found have you added <mvc:annotation-driven/>"
      [requestMappingInfo("path")] | null                                       | null         | "resourceGroupingStrategy is required"
      [requestMappingInfo("path")] | new ClassOrApiAnnotationResourceGrouping() | null         | "swaggerGroup is required"
  }


  def "should group controller paths"() {
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

      ClassOrApiAnnotationResourceGrouping defaultControllerResourceNamingStrategy = new ClassOrApiAnnotationResourceGrouping()

      apiListingReferenceScanner.setResourceGroupingStrategy(defaultControllerResourceNamingStrategy)
      apiListingReferenceScanner.setRequestMappingHandlerMapping([requestMappingHandlerMapping])
      apiListingReferenceScanner.setIncludePatterns([".*"])
      apiListingReferenceScanner.setSwaggerGroup("someGroup")
      apiListingReferenceScanner.setSwaggerPathProvider(new AbsoluteSwaggerPathProvider(servletContext: servletContext()))
      apiListingReferenceScanner.requestMappingEvaluator = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
              RegexRequestMappingPatternMatcher(), newArrayList(".*?"))
      List<ApiListingReference> apiListingReferences = apiListingReferenceScanner.scan()

    then:
      apiListingReferences.size == 1
      ApiListingReference businessListingReference = apiListingReferences[0]
      businessListingReference.getPath() ==
              'http://localhost:8080/context-path/api-docs/someGroup/dummy-class'
  }

  def "grouping of listing references"() {
    given:
      ResourceGroupingStrategy defaultControllerResourceNamingStrategy = new SpringGroupingStrategy()

    when:
      RequestMappingHandlerMapping requestMappingHandlerMapping = Mock()

      requestMappingHandlerMapping.getHandlerMethods() >> [
              (requestMappingInfo("/public/{businessId}"))                   : dummyControllerHandlerMethod(),
              (requestMappingInfo("/public/inventoryTypes"))                 : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/accounts"))          : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/employees"))         : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/inventory"))         : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/inventory/products")): dummyHandlerMethod()
      ]

      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()

    then:
      apiListingReferenceScanner.setResourceGroupingStrategy(defaultControllerResourceNamingStrategy)
      apiListingReferenceScanner.setRequestMappingHandlerMapping([requestMappingHandlerMapping])
      apiListingReferenceScanner.setIncludePatterns([".*"])
      apiListingReferenceScanner.setSwaggerGroup("someGroup")
      apiListingReferenceScanner.setRequestMappingEvaluator(new RequestMappingEvaluator(newArrayList(ApiIgnore),
              new RegexRequestMappingPatternMatcher(), newArrayList(".*?")))
      apiListingReferenceScanner.setSwaggerPathProvider(new AbsoluteSwaggerPathProvider(servletContext: servletContext()))

      List<ApiListingReference> apiListingReferences = apiListingReferenceScanner.scan()
      apiListingReferences.size() == 2
      apiListingReferences.find({ it.getDescription() == 'Dummy Class' })
      apiListingReferences.find({ it.getDescription() == 'Group name' })

    and:
      apiListingReferenceScanner.resourceGroupRequestMappings.size() == 2
      apiListingReferenceScanner.resourceGroupRequestMappings[new ResourceGroup("dummy-controller")].size() == 1
      apiListingReferenceScanner.resourceGroupRequestMappings[new ResourceGroup("dummy-class")].size() == 5
  }

  def "Relative Paths"() {
    given:
      ClassOrApiAnnotationResourceGrouping defaultControllerResourceNamingStrategy = new ClassOrApiAnnotationResourceGrouping()

      RequestMappingHandlerMapping requestMappingHandlerMapping = Mock()

      requestMappingHandlerMapping.getHandlerMethods() >> [
              (requestMappingInfo("/public/{businessId}")): dummyControllerHandlerMethod()
      ]

      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner()
      apiListingReferenceScanner.setSwaggerPathProvider(new RelativeSwaggerPathProvider())
    when:

      apiListingReferenceScanner.setResourceGroupingStrategy(defaultControllerResourceNamingStrategy)
      apiListingReferenceScanner.setRequestMappingHandlerMapping([requestMappingHandlerMapping])
      apiListingReferenceScanner.setIncludePatterns([".*"])
      apiListingReferenceScanner.setSwaggerGroup(["swaggerGroup"])
      apiListingReferenceScanner.requestMappingEvaluator = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
              RegexRequestMappingPatternMatcher(), newArrayList(".*"))
      List<ApiListingReference> apiListingReferences = apiListingReferenceScanner.scan()

    then: "api-docs should not appear in the path"
      ApiListingReference apiListingReference = apiListingReferences[0]
      apiListingReference.getPath() == "/swaggerGroup/group-name"
  }
}