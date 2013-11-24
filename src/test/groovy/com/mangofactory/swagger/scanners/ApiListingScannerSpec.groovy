package com.mangofactory.swagger.scanners

import com.mangofactory.swagger.core.DefaultControllerResourceGroupingStrategy
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.wordnik.swagger.core.SwaggerSpec
import com.wordnik.swagger.model.ApiListing
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Shared
import spock.lang.Specification

import static com.mangofactory.swagger.ScalaUtils.fromScalaList
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE

@Mixin(RequestMappingSupport)
class ApiListingScannerSpec extends Specification {

   @Shared DefaultControllerResourceGroupingStrategy controllerNamingStrategy = new DefaultControllerResourceGroupingStrategy()

   def "Should create an api listing for a single resource grouping "() {
    given:
      RequestMappingInfo requestMappingInfo =
         requestMappingInfo("/some-resource-path",
            [
               consumesRequestCondition: consumesRequestCondition(APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE),
               producesRequestCondition : producesRequestCondition(APPLICATION_JSON_VALUE)
            ]
         )

      RequestMappingContext requestMappingContext = new RequestMappingContext(requestMappingInfo, dummyHandlerMethod())
      Map resourceGroupRequestMappings = [ '/resource-group' : [requestMappingContext]]
      ApiListingScanner scanner = new ApiListingScanner(resourceGroupRequestMappings)

    when:
      List<ApiListing> apiListings = scanner.scan()
    then:
      apiListings.size() == 1
      ApiListing listing = apiListings[0]
      listing.swaggerVersion() == SwaggerSpec.version()
      listing.apiVersion() == "1.0"
      listing.basePath() == ""
      listing.resourcePath() == "/resource-group"
      listing.position() == 0
      fromScalaList(listing.consumes()) == [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE]
      fromScalaList(listing.produces()) == [APPLICATION_JSON_VALUE]
   }

}
