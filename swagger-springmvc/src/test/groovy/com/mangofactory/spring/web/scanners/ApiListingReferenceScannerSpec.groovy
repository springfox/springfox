package com.mangofactory.spring.web.scanners
import com.mangofactory.service.model.ApiListingReference
import com.mangofactory.spring.web.RelativePathProvider
import com.mangofactory.spring.web.SpringGroupingStrategy
import com.mangofactory.spring.web.annotations.ApiIgnore
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.dummy.DummyClass
import com.mangofactory.swagger.dummy.DummyController
import com.mangofactory.swagger.mixins.AccessorAssertions
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.web.AbsolutePathProvider
import com.mangofactory.swagger.web.ClassOrApiAnnotationResourceGrouping
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

import javax.servlet.ServletContext

@Mixin([AccessorAssertions, RequestMappingSupport])
class ApiListingReferenceScannerSpec extends DocumentationContextSpec {

  ApiListingReferenceScanner sut = new ApiListingReferenceScanner()
  RequestMappingHandlerMapping requestMappingHandlerMapping

  def setup() {
    requestMappingHandlerMapping = Mock(RequestMappingHandlerMapping)
    contextBuilder.withHandlerMappings([requestMappingHandlerMapping])
    plugin
            .pathProvider(new AbsolutePathProvider(servletContext: servletContext()))
            .groupName("groupName")
            .excludeAnnotations(ApiIgnore)
            .requestMappingPatternMatcher(new RegexRequestMappingPatternMatcher())
            .includePatterns(".*?")
  }

  def "should not get expected exceptions with invalid constructor params"() {
    given:
      contextBuilder.withHandlerMappings(handlerMappings)

    when:
      def pluginContext = plugin
              .groupName(groupName)
              .build(contextBuilder)

    then:
      pluginContext.groupName == "default"
    where:
      handlerMappings              | resourceGroupingStrategy                   | groupName | message
      [requestMappingInfo("path")] | null                                       | null      | "resourceGroupingStrategy is required"
      [requestMappingInfo("path")] | new ClassOrApiAnnotationResourceGrouping() | null      | "groupName is required"
  }

  def "should group controller paths"() {
    when:
      RequestMappingInfo businessRequestMappingInfo = requestMappingInfo("/api/v1/businesses")
      RequestMappingInfo accountsRequestMappingInfo = requestMappingInfo("/api/v1/accounts")

      requestMappingHandlerMapping.getHandlerMethods() >>
              [
                      (businessRequestMappingInfo): dummyHandlerMethod(),
                      (accountsRequestMappingInfo): dummyHandlerMethod()
              ]

      contextBuilder.withHandlerMappings([requestMappingHandlerMapping])
      def pluginContext = plugin.build(contextBuilder)

      ApiListingReferenceScanResult result = sut.scan(pluginContext)

    then:
      result.getApiListingReferences().size() == 1
      ApiListingReference businessListingReference = result.getApiListingReferences()[0]
      businessListingReference.getPath() ==
              'http://localhost:8080/context-path/api-docs/groupName/dummy-class'
  }

  def "grouping of listing references using Spring grouping strategy"() {
    given:

      requestMappingHandlerMapping.getHandlerMethods() >> [
              (requestMappingInfo("/public/{businessId}"))                   : dummyControllerHandlerMethod(),
              (requestMappingInfo("/public/inventoryTypes"))                 : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/accounts"))          : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/employees"))         : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/inventory"))         : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/inventory/products")): dummyHandlerMethod()
      ]

    when:
      contextBuilder.withHandlerMappings([requestMappingHandlerMapping])
      contextBuilder.withResourceGroupingStrategy(new SpringGroupingStrategy())
      def pluginContext = plugin.build(contextBuilder)
    and:
      ApiListingReferenceScanResult result= sut.scan(pluginContext)

    then:
      result.apiListingReferences.size() == 2
      result.apiListingReferences.find({ it.getDescription() == 'Dummy Class' })
      result.apiListingReferences.find({ it.getDescription() == 'Dummy Controller' })

    and:
      result.resourceGroupRequestMappings.size() == 2
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-controller", DummyController)].size() == 1
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-class", DummyClass)].size() == 5
  }

  def "grouping of listing references using Class or Api Grouping Strategy"() {
    given:

      requestMappingHandlerMapping.getHandlerMethods() >> [
              (requestMappingInfo("/public/{businessId}"))                   : dummyControllerHandlerMethod(),
              (requestMappingInfo("/public/inventoryTypes"))                 : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/accounts"))          : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/employees"))         : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/inventory"))         : dummyHandlerMethod(),
              (requestMappingInfo("/public/{businessId}/inventory/products")): dummyHandlerMethod()
      ]

    when:
      contextBuilder.withHandlerMappings([requestMappingHandlerMapping])
      def pluginContext = plugin.build(contextBuilder)
    and:
      ApiListingReferenceScanResult result= sut.scan(pluginContext)

    then:
      result.apiListingReferences.size() == 2
      result.apiListingReferences.find({ it.getDescription() == 'Dummy Class' })
      result.apiListingReferences.find({ it.getDescription() == 'Group name' })

    and:
      result.resourceGroupRequestMappings.size() == 2
      result.resourceGroupRequestMappings[new ResourceGroup("group-name", DummyController, 2)].size() == 1
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-class", DummyClass)].size() == 5
  }

  def "Relative Paths"() {
    given:
      requestMappingHandlerMapping.getHandlerMethods() >> [
              (requestMappingInfo("/public/{businessId}")): dummyControllerHandlerMethod()
      ]

    when:
      contextBuilder.withHandlerMappings([requestMappingHandlerMapping])
      def pluginContext = plugin
              .pathProvider(new RelativePathProvider(Mock(ServletContext)))
              .build(contextBuilder)
      List<ApiListingReference> apiListingReferences = sut.scan(pluginContext).apiListingReferences

    then: "api-docs should not appear in the path"
      ApiListingReference apiListingReference = apiListingReferences[0]
      apiListingReference.getPath() == "/groupName/group-name"
  }
}