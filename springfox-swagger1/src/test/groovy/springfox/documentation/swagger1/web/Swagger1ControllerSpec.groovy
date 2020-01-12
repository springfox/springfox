/*
 *
 *  Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger1.web

import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.web.util.WebUtils
import spock.lang.Unroll
import springfox.documentation.builders.DocumentationBuilder
import springfox.documentation.service.ApiListing
import springfox.documentation.service.Documentation
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.PropertySourcedMapping
import springfox.documentation.spring.web.WebMvcPropertySourcedRequestMappingHandlerMapping
import springfox.documentation.spring.web.json.JsonSerializer
import springfox.documentation.spring.web.mixins.ApiListingSupport
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.mixins.JsonSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanResult
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanner
import springfox.documentation.spring.web.scanners.ApiListingScanner
import springfox.documentation.swagger1.configuration.SwaggerJacksonModule
import springfox.documentation.swagger1.mixins.MapperSupport

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest

class Swagger1ControllerSpec extends DocumentationContextSpec
    implements MapperSupport,
        JsonSupport,
        AuthSupport,
        ApiListingSupport {

  Swagger1Controller sut =  new Swagger1Controller(
          new DocumentationCache(),
          serviceMapper(),
          new JsonSerializer([new SwaggerJacksonModule()]))


  ApiListingReferenceScanner listingReferenceScanner
  ApiListingScanner listingScanner

  def setup() {
    listingReferenceScanner = Mock(ApiListingReferenceScanner)
    listingScanner = Mock(ApiListingScanner)
    listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult(new HashMap<>())
    listingScanner.scan(_) >> new HashMap<>()
  }

  @Unroll
  def "should return #expectedStatus.value() for #group"() {
    given:
      ApiDocumentationScanner scanner = new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
      sut.documentationCache.addDocumentation(scanner.scan(documentationContext()))
    when:
      def result = sut.getResourceListing(group)
    then:
      result.getStatusCode() == expectedStatus
    where:
      group     | expectedStatus
      null      | HttpStatus.OK
      "default" | HttpStatus.OK
      "unknown" | HttpStatus.NOT_FOUND
  }

  def "should properly replace url"() {
    given:
      def env = Mock(Environment)
      env.getProperty("springfox.documentation.swagger.v1.path") >> "shoes"
      def handler = new WebMvcPropertySourcedRequestMappingHandlerMapping(env, null)
      def method = Swagger1Controller.getMethod("getApiListing", String, String, HttpServletRequest)
      def annotation = method.getAnnotation(PropertySourcedMapping)
    when:
      def path = handler.mappingPath(annotation)
    then:
      "shoes/{swaggerGroup}/{apiDeclaration}" == path
  }

  def "should respond with api listing for a given resource group"() {
    given:
      Map<String, List<ApiListing>> listings = new HashMap<>()
      listings.put('businesses', Arrays.asList(apiListing()))
    and:
      Documentation group = new DocumentationBuilder()
              .name("groupName")
              .apiListingsByResourceGroupName(listings)
              .build()
      sut.documentationCache.addDocumentation(group)
    when:
      def result = sut.getApiListing("groupName", "businesses", servletRequest())
    then:
      result.getStatusCode() == HttpStatus.OK 
  }

  def "should respond with auth included"() {
    given:
      def authTypes = new ArrayList<SecurityScheme>()
      authTypes.add(authorizationTypes())
      Documentation group = new DocumentationBuilder()
              .name("groupName")
              .resourceListing(resourceListing(authTypes))
              .build()

      sut.documentationCache.addDocumentation(group)
    when:
      def result = sut.getResourceListing("groupName")
    then:
      result.getStatusCode() == HttpStatus.OK
      assertDefaultAuth(jsonBodyResponse(result.getBody().value()))
  }

  def servletRequest() {
    def contextPath = "/contextPath"

    HttpServletRequest request = Mock(HttpServletRequest)
    request.contextPath >> contextPath
    request.servletPath >> "/servletPath"
    request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) >> "http://localhost:8080/api-docs"
    request.requestURL >> new StringBuffer("http://localhost/api-docs")
    request.headerNames >> Collections.enumeration([])
    request.servletContext >> servletContext(contextPath)

    request
  }

  def servletContext(String contextPath) {
    ServletContext servletContext = Mock(ServletContext)
    servletContext.contextPath >> contextPath

    servletContext
  }
}