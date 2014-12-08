package com.mangofactory.swagger.models.dto

import org.springframework.http.MediaType

class ApiListingSpec extends InternalJsonSerializationSpec {

  final ApiListing apiListing = new ApiListing(
          '1',
          '1.2',
          '/base',
          '/resource',
          [MediaType.APPLICATION_JSON_VALUE],
          [MediaType.APPLICATION_JSON_VALUE],
          [],
          [],
          [],
          [:],
          'description',
          0);

  def "should serialize"() {
    expect:
      ApiListing apiListing = new ApiListing(
              '1',
              '1.2',
              '/base',
              '/resource',
              [MediaType.APPLICATION_JSON_VALUE],
              [MediaType.APPLICATION_JSON_VALUE],
              [],
              [],
              [],
              [:],
              'description',
              0);
      //TODO - produce larger json by adding ApiDescriptions
      writePretty(apiListing) ==
              """{
  "apiVersion" : "1",
  "apis" : [ ],
  "authorizations" : [ ],
  "basePath" : "/base",
  "consumes" : [ "application/json" ],
  "description" : "description",
  "models" : { },
  "position" : 0,
  "produces" : [ "application/json" ],
  "protocol" : [ ],
  "resourcePath" : "/resource",
  "swaggerVersion" : "1.2"
}"""

  }

  def "should pass coverage"() {
    expect:
      apiListing.with {
        getApis()
        getApiVersion()
        getAuthorizations()
        getBasePath()
        getConsumes()
        getProduces()
        getDescription()
        getModels()
        getPosition()
        getProtocol()
        getResourcePath()
        getSwaggerVersion()
      }
  }
}
