package com.mangofactory.schema

import com.mangofactory.swagger.mixins.ConfiguredObjectMapperSupport
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.service.model.Model
import spock.lang.Specification
import spock.lang.Unroll

@Mixin([TypesForTestingSupport, ModelProviderSupport, ConfiguredObjectMapperSupport])
class UnwrappedTypeSpec extends Specification {
  @Unroll("Unwrapped types are rendered correctly for #typeOfOM")
  def "Unwrapped types are rendered correctly"() {
    given:
      def provider = defaultModelProvider(objectMapperToUse)
      Model asInput = provider.modelFor(ModelContext.inputParam(unwrappedType(), documentationType())).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(unwrappedType(), documentationType())).get()

    expect:
      asInput.getName() == "UnwrappedType"
      asInput.getProperties().size() == 1
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.typeName() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null
      Types.isBaseType(type)

      asReturn.getName() == "UnwrappedType"
      asReturn.getProperties().size() == 1
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.typeName() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null
      Types.isBaseType(type)

    where:
      property    | type      | qualifiedType       | objectMapperToUse             | typeOfOM
      "name"      | "string"  | "java.lang.String"  | objectMapperThatUsesFields()  | "fields"
      "name"      | "string"  | "java.lang.String"  | objectMapperThatUsesGetters() | "getters"
      "name"      | "string"  | "java.lang.String"  | objectMapperThatUsesSetters() | "setters"
  }
}
