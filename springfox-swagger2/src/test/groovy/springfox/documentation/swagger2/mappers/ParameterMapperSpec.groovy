package springfox.documentation.swagger2.mappers

import com.fasterxml.classmate.ResolvedType
import io.swagger.models.Model
import io.swagger.models.parameters.BodyParameter
import io.swagger.models.parameters.FormParameter
import io.swagger.models.parameters.QueryParameter
import io.swagger.models.parameters.SerializableParameter
import io.swagger.models.properties.FileProperty
import spock.lang.Specification
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.ModelRef

class ParameterMapperSpec extends Specification {
  def "body parameters are mapped correctly" () {
    given:
      def parameter = parameter(parameterType).modelRef(modelRef).build()
    when:
      def sut = new ParameterMapper()
    then:
      def mapped = sut.mapParameter(parameter)
    and:
      mapped.access == "access"
      mapped.name == "test"
      mapped.description == "test description"
      mapped.required
      parameterInstance.isAssignableFrom(mapped.class)
      //TODO: check the schema property

    where:
      parameterType  | modelRef                                                 | parameterInstance
      "header"       | new ModelRef("sometype")                                 | SerializableParameter
      "body"         | new ModelRef("sometype")                                 | BodyParameter
      "body"         | new ModelRef("date")                                     | BodyParameter
      "body"         | new ModelRef("sometype")                                 | BodyParameter
      "header"       | new ModelRef("sometype", new ModelRef("itemType"))       | SerializableParameter
      "body"         | new ModelRef("sometype", new ModelRef("itemType"))       | BodyParameter
      "header"       | new ModelRef("sometype", new ModelRef("itemType"), true) | SerializableParameter
      "body"         | new ModelRef("sometype", new ModelRef("itemType"), true) | BodyParameter
  }

  def "form parameters are mapped correctly" () {
    given:
      def parameter = parameter("formData").modelRef(modelRef).build()
    when:
      def sut = new ParameterMapper()
    then:
      def mapped = (FormParameter) sut.mapParameter(parameter)
    and:
      mapped.access == "access"
      mapped.name == "test"
      mapped.description == "test description"
      mapped.required
      mapped.type == type
      mapped.format == format
      mapped.items?.format == itemFormat
      mapped.items?.type == itemType

    where:
      modelRef                                      | type      | format  | itemType       | itemFormat
      new ModelRef("string")                        | "string"  | null    | null           | null
      new ModelRef("array", new ModelRef("string")) | "array"   | null    | "string"       | null
      new ModelRef("array", new ModelRef("int"))    | "array"   | null    | "integer"      | "int32"
      new ModelRef("int")                           | "integer" | "int32" | null           | null
      new ModelRef("long")                          | "integer" | "int64" | null           | null
      new ModelRef("boolean")                       | "boolean" | null    | null           | null
  }

  def "form parameters fall back to body parameters for non-primitive top level types" () {
    given:
      def parameter = parameter("formData")
              .modelRef(new ModelRef("some-non-primitive-type"))
              .build()
    when:
      def sut = new ParameterMapper()
    then:
      sut.mapParameter(parameter) instanceof BodyParameter
  }

  def "file parameter handling" () {
    given:
      def parameter = parameter("formData")
              .modelRef(new ModelRef("file"))
              .build()
    when:
      def sut = new ParameterMapper()
    then:
      sut.mapParameter(parameter) instanceof BodyParameter
  }

  def "form parameters fall back to body parameters for arrays of non-primitive types" () {
    given:
    def parameter = parameter("formData")
            .modelRef(new ModelRef("array", new ModelRef("object")))
            .build()
    when:
    def sut = new ParameterMapper()
    then:
    sut.mapParameter(parameter) instanceof BodyParameter
  }

  def "Serializes byte array to string model in body" () {
    given:
      def byteArray = new ModelRef("", new ModelRef("byte"))
      def parameter = parameter("body").modelRef(byteArray).build()
    when:
      def sut = new ParameterMapper()
    then:
      def mapped = sut.mapParameter(parameter)
    and:
      mapped.access == "access"
      mapped.name == "test"
      mapped.description == "test description"
      mapped.required
      mapped instanceof BodyParameter
      mapped.schema instanceof Model
      mapped.schema.format == "byte"
      mapped.schema.type == "string"
  }

  def "Maps example for body parameter" () {
    given:
      def parameter = parameter("body")
              .modelRef(new ModelRef("sometype"))
              .scalarExample("example")
              .build()
    when:
      def sut = new ParameterMapper()
    then:
      def mapped = sut.mapParameter(parameter)
    and:
      mapped.access == "access"
      mapped.name == "test"
      mapped.description == "test description"
      mapped.required
      mapped instanceof BodyParameter
      mapped.schema instanceof Model
      mapped.schema.example == "example"
  }

  def "Serializes byte array to string model in query" () {
    given:
      def byteArray = new ModelRef("", new ModelRef("byte"))
      def parameter = parameter("query").modelRef(byteArray).build()
    when:
      def sut = new ParameterMapper()
    then:
      def mapped = sut.mapParameter(parameter)
    and:
      mapped.access == "access"
      mapped.name == "test"
      mapped.description == "test description"
      mapped.required
      mapped instanceof QueryParameter
      mapped.format == "byte"
      mapped.type == "string"
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
