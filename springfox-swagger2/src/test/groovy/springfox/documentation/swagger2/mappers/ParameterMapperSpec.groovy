package springfox.documentation.swagger2.mappers

import com.fasterxml.classmate.ResolvedType
import io.swagger.models.Model
import io.swagger.models.parameters.BodyParameter
import io.swagger.models.parameters.QueryParameter
import io.swagger.models.parameters.SerializableParameter
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
