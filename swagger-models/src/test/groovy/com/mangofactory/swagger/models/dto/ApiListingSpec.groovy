package com.mangofactory.swagger.models.dto

import com.mangofactory.swagger.models.dto.builder.ApiListingBuilder
import org.springframework.http.MediaType

class ApiListingSpec extends InternalJsonSerializationSpec {

  final ApiListing apiListing = new ApiListingBuilder()
          .apiVersion('1')
          .swaggerVersion('1.2')
          .basePath('/base')
          .resourcePath('/resource')
          .consumes([MediaType.APPLICATION_JSON_VALUE])
          .produces([MediaType.APPLICATION_JSON_VALUE])
          .protocol([])
          .authorizations([])
          .apis([])
          .models(
          ['someModel':
                   new Model('id', 'name', 'qtype',
                           ['aprop': new ModelProperty('ptype', 'qtype', 0, false, 'pdesc', null, null)]
                           , 'desc', null, null, null)
          ])
          .description('description')
          .position(0)
          .build()

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
