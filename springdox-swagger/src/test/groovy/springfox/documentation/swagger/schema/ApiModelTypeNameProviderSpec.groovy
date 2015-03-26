package springfox.documentation.swagger.schema

import spock.lang.Specification
import springfox.documentation.schema.ExampleWithEnums
import springfox.documentation.schema.TypeWithApiModelAnnotation
import springfox.documentation.schema.TypeWithEmptyApiModelAnnotation

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
