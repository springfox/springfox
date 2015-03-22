package com.mangofactory.swagger.models.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.dto.jackson.SwaggerJacksonModule
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

class InternalJsonSerializationSpec extends Specification {

  @Shared ObjectMapper objectMapper

  def setupSpec() {
    def module = new SwaggerJacksonModule()
    objectMapper = new ObjectMapper()
    objectMapper.registerModule(module)
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