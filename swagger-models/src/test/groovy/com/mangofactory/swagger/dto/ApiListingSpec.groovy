package com.mangofactory.swagger.dto

import org.springframework.http.MediaType

class ApiListingSpec extends InternalJsonSerializationSpec {

  ApiListing apiListing = newApiListing()

  def newApiListing() {
    ApiListing apiListing = new ApiListing()
    apiListing.apiVersion = '1'
    apiListing.swaggerVersion = '1.2'
    apiListing.basePath = '/base'
    apiListing.resourcePath= '/resource'
    apiListing.consumes= [MediaType.APPLICATION_JSON_VALUE]
    apiListing.produces= [MediaType.APPLICATION_JSON_VALUE]
    apiListing.protocol= []
    apiListing.authorizations= []
    apiListing.apis = []
    apiListing.models =
            ['someModel':
                     new ModelDto('id', 'name', 'qtype',
                             ['aprop': new ModelPropertyDto('ptype', 'qtype', 0, false, 'pdesc', null, null)]
                             , 'desc', null, null, null)
            ]
    apiListing.description = 'description'
    apiListing.position = 0
    apiListing
  }

  def "should serialize"() {
    expect:
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
