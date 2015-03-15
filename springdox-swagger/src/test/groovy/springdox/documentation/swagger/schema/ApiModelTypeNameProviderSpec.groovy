package springdox.documentation.swagger.schema

import spock.lang.Specification
import springdox.documentation.schema.ExampleWithEnums
import springdox.documentation.schema.TypeWithApiModelAnnotation
import springdox.documentation.schema.TypeWithEmptyApiModelAnnotation

class ApiModelTypeNameProviderSpec extends Specification {
  def "renders the type names correctly" () {
    given:
      def sut = new ApiModelTypeNameProvider()
    when:
      def name = sut.nameFor(clazz)
    then:
      name == expectedName
    where:
      clazz                           | expectedName
      ExampleWithEnums                | "ExampleWithEnums"
      TypeWithApiModelAnnotation      | "ApiModelTest"
      TypeWithEmptyApiModelAnnotation | "TypeWithEmptyApiModelAnnotation"
  }
}
