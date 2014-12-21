package com.mangofactory.service.model

import com.mangofactory.service.model.builder.ResponseMessageBuilder
import spock.lang.Unroll

class ResponseMessageSpec extends InternalJsonSerializationSpec {

  @Unroll
  def "should serialize"() {
    expect:
      writePretty(responseMessage) == expected

    where:
      responseMessage << [
              new ResponseMessageBuilder().code(200).message("ok").build(),
              new ResponseMessageBuilder().code(200).message("ok").responseModel('model').build()
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
