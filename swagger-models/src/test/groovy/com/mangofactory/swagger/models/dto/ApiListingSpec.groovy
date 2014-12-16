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
              ['someModel': new Model('id', 'name', 'qtype',
                      ['aprop': new ModelProperty('ptype', 'qtype', 0, false, 'pdesc', null, null)]
                      , 'desc', null, null, null)],
              'mdesc',
              0);
      //TODO - produce larger json by adding ApiDescriptions
      writePretty(apiListing) ==
              '''{
  "apiVersion" : "1",
  "swaggerVersion" : "1.2",
  "basePath" : "/base",
  "resourcePath" : "/resource",
  "produces" : [ "application/json" ],
  "consumes" : [ "application/json" ],
  "apis" : [ ],
  "models" : {
    "someModel" : {
      "description" : "desc",
      "id" : "id",
      "properties" : {
        "aprop" : {
          "description" : "pdesc",
          "required" : false,
          "type" : "ptype"
        }
      }
    }
  }
}'''

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
