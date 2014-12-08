package com.mangofactory.swagger.models.property.field
import com.mangofactory.swagger.mixins.ModelPropertyLookupSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.ModelContext
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.dto.AllowableListValues
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport])
class FieldModelPropertySpec extends Specification {
  def "Extracting information from resolved fields" () {
    given:
      def typeToTest = typeForTestingGettersAndSetters()
      def modelContext = ModelContext.inputParam(typeToTest )
      def field = field(typeToTest, fieldName)
      def sut = new FieldModelProperty(fieldName, field, new AlternateTypeProvider())

    expect:
      sut.propertyDescription() == description
      sut.required == isRequired
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      if (allowableValues != null) {
        def values = newArrayList(allowableValues)
        sut.allowableValues() == new AllowableListValues(values, "string")
      } else {
        sut.allowableValues() == null
      }
      sut.getName() == fieldName
      sut.getType() == field.getType()

    where:
    fieldName       || description          | isRequired | typeName             | qualifiedTypeName                                               | allowableValues
    "intProp"       || "int Property Field" | true       | "int"                | "int"                                                           | null
    "boolProp"      || null                 | false      | "boolean"            | "boolean"                                                       | null
    "enumProp"      || null                 | false      | "string"             | "com.mangofactory.swagger.models.ExampleEnum"                   | ["ONE", "TWO"]
    "genericProp"   || null                 | false      | "GenericType«string»"| "com.mangofactory.swagger.models.GenericType<java.lang.String>" | null
  }

  def "Extracting information from generic fields with array type binding" () {
    given:
      def typeToTest = typeForTestingGettersAndSetters()
      def modelContext = ModelContext.inputParam(typeToTest )
      def field = field(typeToTest, fieldName)
      def sut = new FieldModelProperty(fieldName, field, new AlternateTypeProvider())

    expect:
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.getName() == fieldName
      sut.getType() == field.getType()


    where:
      fieldName             || typeName                       | qualifiedTypeName
      "genericByteArray"    || "GenericType«Array«byte»»"     | "com.mangofactory.swagger.models.GenericType<byte[]>"
      "genericCategoryArray"|| "GenericType«Array«Category»»" | "com.mangofactory.swagger.models.GenericType<com.mangofactory.swagger.models.Category[]>"
  }
}
