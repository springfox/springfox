package com.mangofactory.swagger.models.property.field

import com.google.common.collect.Lists
import com.mangofactory.swagger.mixins.ModelPropertySupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.ModelContext
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import spock.lang.Specification

@Mixin([TypesForTestingSupport, ModelPropertySupport])
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
        sut.allowableValues() == Lists.newArrayList(allowableValues)
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
}
