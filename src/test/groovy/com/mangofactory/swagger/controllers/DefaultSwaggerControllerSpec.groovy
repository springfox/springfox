package com.mangofactory.swagger.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.mangofactory.swagger.mixins.ApiListingSupport
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.web.servlet.View
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@Mixin([com.mangofactory.swagger.mixins.JsonSupport, ApiListingSupport])
class DefaultSwaggerControllerSpec extends Specification {

   @Shared
   MockMvc mockMvc
   @Shared
   View mockView
   @Shared
   DefaultSwaggerController controller = new DefaultSwaggerController()

   def setup() {
      def jackson2 = new MappingJackson2HttpMessageConverter()
      ObjectMapper mapper = new ObjectMapper()
      mapper.registerModule(new DefaultScalaModule())

      jackson2.setObjectMapper(mapper)
      mockMvc = standaloneSetup(controller)
              .setSingleView(mockView)
              .setMessageConverters(jackson2)
              .build();
   }

   @Unroll("path: #path")
   def "should return the default or first swagger resource listing"() {
    given:
      controller.setSwaggerApiResourceListingMap([default: defaultSwaggerResourceListing()])
    when:
      MvcResult result = mockMvc.perform(get(path)).andDo(print()).andReturn()
      def responseJson = jsonBodyResponse(result)
    then:
      result.getResponse().getStatus() == expectedStatus
    where:
      path                | expectedStatus
      "/api-docs"         | 200
      "/api-docs/default" | 200
      "/api-docs/unknown" | 404
   }

   def "should respond with api listing for a given resource group"() {
    given:
      controller.setSwaggerApiListings([resourceKey: ['businesses': apiListing()]])

    when:
      MvcResult result = mockMvc.perform(get("/api-docs/resourceKey/businesses")).andDo(print()).andReturn()
      def responseJson = jsonBodyResponse(result)

    then:
      result.getResponse().getStatus() == 200

   }

   private SwaggerApiResourceListing defaultSwaggerResourceListing() {
      def listing = new SwaggerApiResourceListing()
      listing.with {
         initialize()
      }
      return listing
   }
}
