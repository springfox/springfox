package com.mangofactory.swagger.models.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.dto.jackson.SwaggerJacksonProvider
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

class InternalJsonSerializationSpec extends Specification {

  @Shared ObjectMapper objectMapper

  def setupSpec() {
    def provider = new SwaggerJacksonProvider()
    objectMapper = provider.objectMapper
//    objectMapper.registerModule(provider.swaggerJacksonModule())
  }

  def writeAndParse(object, boolean print = true) {

    def jsonString = objectMapper.writeValueAsString(object)
    def json = new JsonSlurper().parseText(jsonString)
    if (print) {
      println jsonString
    }
    json
  }

  def writePretty(object){
    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object)
  }
}