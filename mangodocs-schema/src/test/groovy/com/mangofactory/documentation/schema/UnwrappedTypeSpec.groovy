package com.mangofactory.documentation.schema

import com.mangofactory.documentation.schema.mixins.ConfiguredObjectMapperSupport
import com.mangofactory.documentation.schema.mixins.ModelProviderSupport
import com.mangofactory.documentation.schema.mixins.TypesForTestingSupport
import com.mangofactory.documentation.spi.schema.contexts.ModelContext
import spock.lang.Specification
import spock.lang.Unroll

import static com.mangofactory.documentation.spi.DocumentationType.*

@Mixin([TypesForTestingSupport, ModelProviderSupport, ConfiguredObjectMapperSupport, AlternateTypesSupport])
class UnwrappedTypeSpec extends Specification {
  @Unroll("Unwrapped types are rendered correctly for #typeOfOM")
  def "Unwrapped types are rendered correctly"() {
    given:
      def provider = defaultModelProvider(objectMapperToUse)
      Model asInput = provider.modelFor(ModelContext.inputParam(unwrappedType(), SWAGGER_12, alternateTypeProvider())).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(unwrappedType(), SWAGGER_12, alternateTypeProvider())).get()

    expect:
      asInput.getName() == "UnwrappedType"
      asInput.getProperties().size() == 1
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null

      asReturn.getName() == "UnwrappedType"
      asReturn.getProperties().size() == 1
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null

    where:
      property    | type    | qualifiedType       | objectMapperToUse             | typeOfOM
      "name"      | String  | "java.lang.String"  | objectMapperThatUsesFields()  | "fields"
      "name"      | String  | "java.lang.String"  | objectMapperThatUsesGetters() | "getters"
      "name"      | String  | "java.lang.String"  | objectMapperThatUsesSetters() | "setters"
  }
}
