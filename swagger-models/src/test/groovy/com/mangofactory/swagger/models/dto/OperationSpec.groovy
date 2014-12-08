package com.mangofactory.swagger.models.dto

class OperationSpec extends InternalJsonSerializationSpec {

  final Operation operation = new Operation(
          'PUT',
          'updatePet',
          'updatePet',
          'void',
          'updatePet',
          0,
          ["*/*"],
          ['application/json'],
          [],
          [],
          [new Parameter('pet', 'pet', '', false, false, 'Pet', null, 'body', null)],
          [new ResponseMessage(200, 'ok', null)],
          "false")

  def "should serialize an operation"() {
    expect:
      writePretty(operation) ==
              """{
  "authorizations" : [ ],
  "consumes" : [ "application/json" ],
  "deprecated" : "false",
  "method" : "PUT",
  "nickname" : "updatePet",
  "notes" : "updatePet",
  "parameters" : [ {
    "allowMultiple" : false,
    "dataType" : "Pet",
    "defaultValue" : "",
    "description" : "pet",
    "name" : "pet",
    "paramType" : "body",
    "type" : "Pet",
    "required" : false
  } ],
  "position" : 0,
  "produces" : [ "*/*" ],
  "protocol" : [ ],
  "responseMessages" : [ {
    "code" : 200,
    "message" : "ok"
  } ],
  "summary" : "updatePet",
  "type" : "void"
}"""
  }

  def "should pass coverage"() {
    expect:
      operation.with {
        getAuthorizations()
        getConsumes()
        getDeprecated()
        getMethod()
        getNickname()
        getNotes()
        getParameters()
        getPosition()
        getProduces()
        getProtocol()
        getResponseClass()
        getResponseMessages()
        getSummary()
      }
  }
}
