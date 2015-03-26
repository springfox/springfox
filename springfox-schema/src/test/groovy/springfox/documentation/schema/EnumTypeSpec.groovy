package springfox.documentation.schema

import spock.lang.Specification
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.spi.DocumentationType

import static com.google.common.collect.Lists.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport, AlternateTypesSupport])
class EnumTypeSpec extends Specification {
  def "enum type are inferred as type string with allowable values" () {
    given:
      def list = newArrayList("ONE", "TWO")
      def provider = defaultModelProvider()
      def namingStrategy = new DefaultGenericTypeNamingStrategy()
      Model asInput = provider.modelFor(inputParam(enumType(), DocumentationType.SWAGGER_12,
              alternateTypeProvider(), namingStrategy)).get()
      Model asReturn = provider.modelFor(returnValue(enumType(), DocumentationType.SWAGGER_12,
              alternateTypeProvider(), namingStrategy)).get()

    expect:
      asInput.getName() == "ExampleWithEnums"
      asInput.getProperties().containsKey("exampleEnum")
      def modelPropertyOption = asInput.getProperties().get("exampleEnum")
      def modelProperty = modelPropertyOption


      modelProperty.type.erasedType == ExampleEnum
      modelProperty.getQualifiedType() == "springfox.documentation.schema.ExampleEnum"
      modelProperty.getModelRef().type == "string"
      !modelProperty.getModelRef().collection
      modelProperty.getModelRef().itemType == null
      modelProperty.getAllowableValues().getValues() == list

      asReturn.getName() == "ExampleWithEnums"
      asReturn.getProperties().containsKey("exampleEnum")
      def retModelPropertyOption = asReturn.getProperties().get("exampleEnum")
      def retModelProperty = retModelPropertyOption
      retModelProperty.type.erasedType == ExampleEnum
      retModelProperty.getQualifiedType() == "springfox.documentation.schema.ExampleEnum"
      retModelProperty.getModelRef().type == "string"
      !retModelProperty.getModelRef().collection
      retModelProperty.getModelRef().itemType == null 
      retModelProperty.getAllowableValues().getValues() == list
  }
}
