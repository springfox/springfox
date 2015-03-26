package springfox.documentation.spring.web.plugins

import spock.lang.Specification
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.OperationModelContextsBuilder
import springfox.documentation.spring.web.dummy.models.Example

class OperationModelsBuilderSpec extends Specification {
  OperationModelContextsBuilder sut =
          new OperationModelContextsBuilder(DocumentationType.SWAGGER_12, Mock(AlternateTypeProvider), Mock(GenericTypeNamingStrategy))
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
