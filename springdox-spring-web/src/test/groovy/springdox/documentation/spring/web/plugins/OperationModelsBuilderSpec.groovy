package springdox.documentation.spring.web.plugins

import spock.lang.Specification
import springdox.documentation.spi.DocumentationType
import springdox.documentation.spi.schema.AlternateTypeProvider
import springdox.documentation.spi.service.contexts.OperationModelContextsBuilder
import springdox.documentation.spring.web.dummy.models.Example

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
