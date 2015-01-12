package com.mangofactory.spring.web.plugins

import com.mangofactory.schema.plugins.DocumentationType
import com.mangofactory.swagger.dummy.models.Example
import spock.lang.Specification

class OperationModelsBuilderSpec extends Specification {
  OperationModelsBuilder sut = new OperationModelsBuilder(DocumentationType.SWAGGER_12)
  def "Manages a unique set of model contexts" () {
    given:
      sut.addInputParam(Example)
    when:
      def models = sut.build()
    then:
      models.size() == 1

    and:
      sut.addInputParam(Example).build().size() == 1
      sut.addReturn(Example).build().size() == 2
  }

}
