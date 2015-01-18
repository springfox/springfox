package com.mangofactory.documentation.spring.web.plugins

import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.schema.AlternateTypeProvider
import com.mangofactory.documentation.spi.service.contexts.OperationModelContextsBuilder
import com.mangofactory.documentation.spring.web.dummy.models.Example
import spock.lang.Specification

class OperationModelsBuilderSpec extends Specification {
  OperationModelContextsBuilder sut =
          new OperationModelContextsBuilder(DocumentationType.SWAGGER_12, Mock(AlternateTypeProvider))
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
