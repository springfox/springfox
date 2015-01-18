package com.mangofactory.service.model.builder

import com.mangofactory.documentation.service.model.ResponseMessage
import com.mangofactory.documentation.service.model.builder.OperationBuilder
import com.mangofactory.documentation.service.model.builder.ResponseMessageBuilder
import spock.lang.Specification

import static com.google.common.collect.Sets.newHashSet

class OperationBuilderSpec extends Specification {
  OperationBuilder sut = new OperationBuilder()
  ResponseMessage partialOk = new ResponseMessageBuilder().code(200).message(null).responseModel(null).build()
  ResponseMessage fullOk = new ResponseMessageBuilder().code(200).message("OK").responseModel("String").build()

  def "Merges response messages when new response messages are applied" () {
    given:
      sut.responseMessages(newHashSet(partialOk))
    when:
      sut.responseMessages(newHashSet(fullOk))
    and:
      def operation = sut.build()
    then:
      operation.responseMessages.size() == 1
      operation.responseMessages.first().code == 200
      operation.responseMessages.first().message == "OK"
      operation.responseMessages.first().responseModel == "String"
  }

  def "Response message builder is non-destructive" () {
    given:
      sut.responseMessages(newHashSet(fullOk))
    when:
      sut.responseMessages(newHashSet(partialOk))
    and:
      def operation = sut.build()
    then:
      operation.responseMessages.size() == 1
      operation.responseMessages.first().code == 200
      operation.responseMessages.first().message == "OK"
      operation.responseMessages.first().responseModel == "String"
  }

  def "String properties are non destructive" () {
    given:
      sut
        .deprecated("deprecated")
        .method("method")
        .nickname("nickname")
        .notes("notes")
        .summary("summary")
        .hidden(true)
        .position(1)
    when:
      sut
        .deprecated(null)
        .method(null)
        .nickname(null)
        .notes(null)
        .summary(null)
    and:
      def operation = sut.build()
    then:
      operation.deprecated == "deprecated"
      operation.method == "method"
      operation.nickname == "nickname"
      operation.notes == "notes"
      operation.summary == "summary"
  }
}
