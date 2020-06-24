package springfox.documentation.builders

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.ModelSpecification
import springfox.documentation.service.ParameterStyle
import springfox.documentation.service.ParameterType

import static springfox.documentation.schema.ScalarType.*
import static springfox.documentation.service.CollectionFormat.*

@Ignore
class RequestParameterBuilderSpec extends Specification {
  @Unroll
  def "Query parameter are built as simple parameters for scalar types"() {
    given:
    def sut = baseRequestParameter(ParameterType.QUERY)

    when:
    def actual = sut.query { q ->
      q.style(style)
          .explode(explode)
          .allowReserved(allowReserved)
          .allowEmptyValue(allowEmpty)
          .model { it.copyOf(model) }
          .collectionFormat(collectionFormat)
    }.build()

    then:
    actual.parameterSpecification.query.isPresent()

    and:
    def scalar = actual.parameterSpecification.query.get()
    scalar.facets.size() == 0
    scalar.allowReserved == allowReserved
    scalar.allowEmptyValue == allowEmpty
    scalar.explode == explode
    scalar.style == style
    scalar.collectionFormat == NONE
    scalar.model.scalar.map { s -> s.type }.orElse(null) == model.scalar.get().type

    where:
    style                         | explode | allowReserved | model        | allowEmpty | collectionFormat | facets
    ParameterStyle.MATRIX         | false   | false         | spec(STRING) | false      | NONE             | []
    ParameterStyle.DEEPOBJECT     | false   | false         | spec(STRING) | false      | NONE             | []
    ParameterStyle.FORM           | false   | false         | spec(STRING) | false      | NONE             | []
    ParameterStyle.LABEL          | false   | false         | spec(STRING) | false      | NONE             | []
    ParameterStyle.PIPEDELIMITED  | false   | false         | spec(STRING) | false      | NONE             | []
    ParameterStyle.SIMPLE         | false   | false         | spec(STRING) | false      | NONE             | []
    ParameterStyle.SPACEDELIMITED | false   | false         | spec(STRING) | false      | NONE             | []
    ParameterStyle.MATRIX         | false   | false         | spec(STRING) | false      | CSV              | []
    ParameterStyle.DEEPOBJECT     | false   | false         | spec(STRING) | false      | CSV              | []
    ParameterStyle.FORM           | false   | false         | spec(STRING) | false      | CSV              | []
    ParameterStyle.LABEL          | false   | false         | spec(STRING) | false      | CSV              | []
    ParameterStyle.PIPEDELIMITED  | false   | false         | spec(STRING) | false      | CSV              | []
    ParameterStyle.SIMPLE         | false   | false         | spec(STRING) | false      | CSV              | []
    ParameterStyle.SPACEDELIMITED | false   | false         | spec(STRING) | false      | CSV              | []
    ParameterStyle.MATRIX         | false   | false         | spec(STRING) | true       | TSV              | []
    ParameterStyle.DEEPOBJECT     | false   | false         | spec(STRING) | true       | TSV              | []
    ParameterStyle.FORM           | false   | false         | spec(STRING) | true       | TSV              | []
    ParameterStyle.LABEL          | false   | false         | spec(STRING) | true       | TSV              | []
    ParameterStyle.PIPEDELIMITED  | false   | false         | spec(STRING) | true       | TSV              | []
    ParameterStyle.SIMPLE         | false   | false         | spec(STRING) | true       | TSV              | []
    ParameterStyle.SPACEDELIMITED | false   | false         | spec(STRING) | true       | TSV              | []

  }


  private RequestParameterBuilder baseRequestParameter(ParameterType type) {
    new RequestParameterBuilder().name("test")
        .required(true)
        .in(type)
        .deprecated(false)
        .description("This is a test")
        .extensions([])
        .hidden(false)
        .precedence(1)
  }

  ModelSpecification spec(type) {
    new ModelSpecificationBuilder()
        .name("test")
        .scalarModel(type)
        .build()
  }
}
