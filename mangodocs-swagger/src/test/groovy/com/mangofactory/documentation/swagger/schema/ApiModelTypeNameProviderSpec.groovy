package com.mangofactory.documentation.swagger.schema
import com.mangofactory.documentation.schema.ExampleWithEnums
import com.mangofactory.documentation.schema.TypeWithApiModelAnnotation
import com.mangofactory.documentation.schema.TypeWithEmptyApiModelAnnotation
import spock.lang.Specification

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
