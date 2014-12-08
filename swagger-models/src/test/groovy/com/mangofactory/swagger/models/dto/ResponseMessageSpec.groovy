package com.mangofactory.swagger.models.dto

import spock.lang.Unroll

class ResponseMessageSpec extends InternalJsonSerializationSpec {

  @Unroll
  def "should serialize"() {
    expect:
      writePretty(responseMessage) == expected

    where:
      responseMessage << [
              new ResponseMessage(200, "ok", null),
              new ResponseMessage(200, "ok", 'model')
      ]

      expected << [
              """{
  "code" : 200,
  "message" : "ok"
}""",
              """{
  "code" : 200,
  "message" : "ok",
  "responseModel" : "model"
}"""
      ]

  }

  def "should pass coverage"() {
    expect:
      def message = new ResponseMessage(200, "ok", 'model')
      message.getCode()
      message.getMessage()
      message.getResponseModel()
  }
}
