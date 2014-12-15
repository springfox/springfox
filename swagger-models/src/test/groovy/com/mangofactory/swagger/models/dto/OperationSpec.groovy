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
          [new ResponseMessage(200, 'ok', null)] as Set,
          "false")

  def "should serialize an operation"() {
    expect:
      writePretty(operation) =='''{
  "method" : "PUT",
  "summary" : "updatePet",
  "notes" : "updatePet",
  "nickname" : "updatePet",
  "produces" : [ "*/*" ],
  "consumes" : [ "application/json" ],
  "parameters" : [ {
    "allowMultiple" : false,
    "defaultValue" : "",
    "description" : "pet",
    "name" : "body",
    "paramType" : "body",
    "type" : "Pet",
    "required" : false
  } ],
  "responseMessages" : [ {
    "code" : 200,
    "message" : "ok"
  } ],
  "deprecated" : "false",
  "type" : "void"
}'''
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
