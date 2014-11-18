package com.mangofactory.swagger.models

import com.mangofactory.swagger.mixins.ConfiguredObjectMapperSupport
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.model.Model
import spock.lang.Specification
import spock.lang.Unroll

@Mixin([TypesForTestingSupport, ModelProviderSupport, ConfiguredObjectMapperSupport])
class UnwrappedTypeSpec extends Specification {
  @Unroll("Unwrapped types are rendered correctly for #typeOfOM")
  def "Unwrapped types are rendered correctly"() {
    given:
      def provider = defaultModelProvider(objectMapperToUse)
      Model asInput = provider.modelFor(ModelContext.inputParam(unwrappedType())).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(unwrappedType())).get()

    expect:
      asInput.name() == "UnwrappedType"
      asInput.properties().size() == 1
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      modelProperty.get().qualifiedType() == qualifiedType
      modelProperty.get().items().isEmpty()
      Types.isBaseType(type)

      asReturn.name() == "UnwrappedType"
      asReturn.properties().size() == 1
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == type
      retModelProperty.get().qualifiedType() == qualifiedType
      retModelProperty.get().items().isEmpty()
      Types.isBaseType(type)

    where:
      property    | type      | qualifiedType       | objectMapperToUse             | typeOfOM
      "name"      | "string"  | "java.lang.String"  | objectMapperThatUsesFields()  | "fields"
      "name"      | "string"  | "java.lang.String"  | objectMapperThatUsesGetters() | "getters"
      "name"      | "string"  | "java.lang.String"  | objectMapperThatUsesSetters() | "setters"
  }
}
