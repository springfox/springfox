package springfox.documentation.swagger2.mappers
import com.fasterxml.classmate.ResolvedType
import io.swagger.models.parameters.SerializableParameter
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.AllowableListValues
import springfox.documentation.service.Parameter

import static springfox.documentation.swagger2.mappers.SerializableParameterFactories.*

class SerializableParameterFactoriesSpec extends Specification {
  def "Cannot instantiate static class" () {
    when:
      new SerializableParameterFactories()
    then:
      thrown(UnsupportedOperationException)
  }

  @Unroll
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
        if (modelRef.itemType.equals("byte")) {
          ((SerializableParameter) mapped).format == "byte"
          ((SerializableParameter) mapped).type == "string"
        } else {
          ((SerializableParameter) mapped).collectionFormat == "csv"
          ((SerializableParameter) mapped).type == "array"
          ((SerializableParameter) mapped).items.name == modelRef.itemType
        }
      } else {
        ((SerializableParameter)mapped).format == null
        ((SerializableParameter)mapped).type == "string"
      }
    where:
      parameterType  | modelRef
      "header"       | new ModelRef("sometype")
      "query"        | new ModelRef("sometype")
      "form"         | new ModelRef("sometype")
      "form"         | new ModelRef("sometype", new ModelRef("byte"))
      "cookie"       | new ModelRef("sometype")
      "path"         | new ModelRef("sometype")
      "header"       | new ModelRef("sometype", new ModelRef("itemType"))
      "query"        | new ModelRef("sometype", new ModelRef("itemType"))
      "form"         | new ModelRef("sometype", new ModelRef("itemType"))
      "cookie"       | new ModelRef("sometype", new ModelRef("itemType"))
      "path"         | new ModelRef("sometype", new ModelRef("itemType"))
      "header"       | new ModelRef("sometype", new ModelRef("itemType"), true)
      "query"        | new ModelRef("sometype", new ModelRef("itemType"), true)
      "form"         | new ModelRef("sometype", new ModelRef("itemType"), true)
      "cookie"       | new ModelRef("sometype", new ModelRef("itemType"), true)
      "path"         | new ModelRef("sometype", new ModelRef("itemType"), true)
  }

  @Unroll
  def "Known serializable parameters are mapped with allowable values" () {
    given:
      Parameter parameter = parameterWithAllowableValues(parameterType)
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
      ((SerializableParameter)mapped).enum.size() == 3
    where:
      parameterType  | modelRef
      "header"       | new ModelRef("sometype")
      "query"        | new ModelRef("sometype")
      "form"         | new ModelRef("sometype")
      "cookie"       | new ModelRef("sometype")
      "path"         | new ModelRef("sometype")
      "header"       | new ModelRef("sometype", new ModelRef("itemType"))
      "query"        | new ModelRef("sometype", new ModelRef("itemType"))
      "form"         | new ModelRef("sometype", new ModelRef("itemType"))
      "cookie"       | new ModelRef("sometype", new ModelRef("itemType"))
      "path"         | new ModelRef("sometype", new ModelRef("itemType"))
      "header"       | new ModelRef("sometype", new ModelRef("itemType"), true)
      "query"        | new ModelRef("sometype", new ModelRef("itemType"), true)
      "form"         | new ModelRef("sometype", new ModelRef("itemType"), true)
      "cookie"       | new ModelRef("sometype", new ModelRef("itemType"), true)
      "path"         | new ModelRef("sometype", new ModelRef("itemType"), true)
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

  def parameterWithAllowableValues(def parameterType) {
    new ParameterBuilder()
        .name("test")
        .parameterAccess("access")
        .parameterType(parameterType)
        .description("test description")
        .required(true)
        .type(Mock(ResolvedType))
        .allowableValues(new AllowableListValues(["ABC", "ONE", "TWO"], "string"))
  }
}
