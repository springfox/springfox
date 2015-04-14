package springfox.documentation.swagger2.mappers
import com.fasterxml.classmate.ResolvedType
import com.wordnik.swagger.models.parameters.SerializableParameter
import spock.lang.Specification
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.Parameter

import static springfox.documentation.swagger2.mappers.SerializableParameterFactories.*

class SerializableParameterFactoriesSpec extends Specification {
  def "Cannot instantiate static class" () {
    when:
      new SerializableParameterFactories()
    then:
      thrown(UnsupportedOperationException)
  }

  def "Known serializable parameters are mapped" () {
    given:
      Parameter parameter = parameter(parameterType)
          .modelRef(modelRef)
          .build()
    when:
      def sut = create(parameter)
    then:
      sut.isPresent()
    and:
      def mapped = sut.get()
      mapped.access == "access"
      mapped.name == "test"
      mapped.description == "test description"
      mapped.required
      mapped instanceof SerializableParameter
      if (modelRef.isCollection()) {
        ((SerializableParameter)mapped).collectionFormat == "csv"
        ((SerializableParameter)mapped).type == "array"
        ((SerializableParameter)mapped).items.name == modelRef.itemType
      } else {
        ((SerializableParameter)mapped).format == null
        ((SerializableParameter)mapped).type == "string"
      }
    where:
      parameterType  | modelRef
      "header"       | new ModelRef("sometype")
      "query"        | new ModelRef("sometype")
      "form"         | new ModelRef("sometype")
      "cookie"       | new ModelRef("sometype")
      "path"         | new ModelRef("sometype")
      "header"       | new ModelRef("sometype", "itemType")
      "query"        | new ModelRef("sometype", "itemType")
      "form"         | new ModelRef("sometype", "itemType")
      "cookie"       | new ModelRef("sometype", "itemType")
      "path"         | new ModelRef("sometype", "itemType")
      "header"       | new ModelRef("sometype", "itemType", true)
      "query"        | new ModelRef("sometype", "itemType", true)
      "form"         | new ModelRef("sometype", "itemType", true)
      "cookie"       | new ModelRef("sometype", "itemType", true)
      "path"         | new ModelRef("sometype", "itemType", true)
  }

  def parameter(def parameterType) {
    new ParameterBuilder()
        .name("test")
        .parameterAccess("access")
        .parameterType(parameterType)
        .description("test description")
        .required(true)
        .type(Mock(ResolvedType))
  }
}
