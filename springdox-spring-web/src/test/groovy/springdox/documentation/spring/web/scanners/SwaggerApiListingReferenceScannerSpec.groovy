package springdox.documentation.spring.web.scanners

import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import springdox.documentation.annotations.ApiIgnore
import springdox.documentation.service.ApiListingReference
import springdox.documentation.service.ResourceGroup
import springdox.documentation.spring.web.RelativePathProvider
import springdox.documentation.spring.web.SpringGroupingStrategy
import springdox.documentation.spring.web.dummy.DummyClass
import springdox.documentation.spring.web.dummy.DummyController
import springdox.documentation.spring.web.mixins.AccessorAssertions
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

import javax.servlet.ServletContext

@Mixin([AccessorAssertions, RequestMappingSupport])
class ApiListingReferenceScannerSpec extends DocumentationContextSpec {

  ApiListingReferenceScanner sut = new ApiListingReferenceScanner()
  RequestMappingHandlerMapping requestMappingHandlerMapping

  def setup() {
    requestMappingHandlerMapping = Mock(RequestMappingHandlerMapping)
    contextBuilder.handlerMappings([requestMappingHandlerMapping])
            .withResourceGroupingStrategy(new SpringGroupingStrategy())
    plugin
            .pathProvider(new RelativePathProvider(servletContext()))
            .groupName("groupName")
            .excludeAnnotations(ApiIgnore)
            .requestMappingPatternMatcher(new RegexRequestMappingPatternMatcher())
            .includePatterns(".*?")
  }

  def "should not get expected exceptions with invalid constructor params"() {
    given:
      contextBuilder.handlerMappings(handlerMappings)

    when:
      plugin
              .groupName(groupName)
              .configure(contextBuilder)

    then:
      context().groupName == "default"
    where:
      handlerMappings              | resourceGroupingStrategy     | groupName | message
      [requestMappingInfo("path")] | null                         | null      | "resourceGroupingStrategy is required"
      [requestMappingInfo("path")] | new SpringGroupingStrategy() | null      | "groupName is required"
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

      contextBuilder.handlerMappings([requestMappingHandlerMapping])
      plugin.configure(contextBuilder)

      ApiListingReferenceScanResult result = sut.scan(context())

    then:
      result.getApiListingReferences().size() == 1
      ApiListingReference businessListingReference = result.getApiListingReferences()[0]
      businessListingReference.getPath() ==
              '/groupName/dummy-class'
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
      contextBuilder.handlerMappings([requestMappingHandlerMapping])
      contextBuilder.withResourceGroupingStrategy(new SpringGroupingStrategy())
      plugin.configure(contextBuilder)
    and:
      ApiListingReferenceScanResult result = sut.scan(context())

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
      contextBuilder.handlerMappings([requestMappingHandlerMapping])
      plugin.configure(contextBuilder)
    and:
      ApiListingReferenceScanResult result = sut.scan(context())

    then:
      result.apiListingReferences.size() == 2
      result.apiListingReferences.find({ it.getDescription() == 'Dummy Class' })
      result.apiListingReferences.find({ it.getDescription() == 'Dummy Controller' })

    and:
      result.resourceGroupRequestMappings.size() == 2
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-controller", DummyController)].size() == 1
      result.resourceGroupRequestMappings[new ResourceGroup("dummy-class", DummyClass)].size() == 5
  }

  def "Relative Paths"() {
    given:
      requestMappingHandlerMapping.getHandlerMethods() >> [
              (requestMappingInfo("/public/{businessId}")): dummyControllerHandlerMethod()
      ]

    when:
      contextBuilder.handlerMappings([requestMappingHandlerMapping])
      plugin.pathProvider(new RelativePathProvider(Mock(ServletContext)))
      List<ApiListingReference> apiListingReferences = sut.scan(context()).apiListingReferences

    then: "api-docs should not appear in the path"
      ApiListingReference apiListingReference = apiListingReferences[0]
      apiListingReference.getPath() == "/groupName/dummy-controller"
  }
}