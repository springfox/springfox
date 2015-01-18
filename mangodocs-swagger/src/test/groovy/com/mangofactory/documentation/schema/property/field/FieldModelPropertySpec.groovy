package com.mangofactory.documentation.schema.property.field

import com.mangofactory.documentation.schema.AlternateTypesSupport
import com.mangofactory.documentation.schema.SchemaSpecification
import com.mangofactory.documentation.schema.TypeWithGettersAndSetters
import com.mangofactory.documentation.schema.mixins.ModelPropertyLookupSupport
import com.mangofactory.documentation.schema.mixins.TypesForTestingSupport
import com.mangofactory.documentation.service.model.AllowableListValues

import static com.google.common.collect.Lists.*
import static com.mangofactory.documentation.spi.DocumentationType.*
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, AlternateTypesSupport])
class FieldModelPropertySpec extends SchemaSpecification {
  def "Extracting information from resolved fields" () {
    given:
      def modelContext = inputParam(TypeWithGettersAndSetters, SWAGGER_12, alternateTypeProvider())
      def field = field(TypeWithGettersAndSetters, fieldName)
      def sut = new FieldModelProperty(fieldName, field, alternateTypeProvider())

    expect:
      sut.propertyDescription() == null //documentationType(): Added test
      !sut.required
      typeNameExtractor.typeName(fromParent(modelContext, sut.getType())) == typeName
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
    "enumProp"      || null                 | false      | "string"             | "com.mangofactory.documentation.schema.ExampleEnum"                   | ["ONE", "TWO"]
    "genericProp"   || null                 | false      | "GenericType«string»"| "com.mangofactory.documentation.schema.GenericType<java.lang.String>" | null
  }

  def "Extracting information from generic fields with array type binding" () {
    given:
      def typeToTest = TypeWithGettersAndSetters
      def modelContext = inputParam(typeToTest, SWAGGER_12, alternateTypeProvider())
      def field = field(typeToTest, fieldName)
      def sut = new FieldModelProperty(fieldName, field, alternateTypeProvider())

    expect:
      typeNameExtractor.typeName(fromParent(modelContext, sut.getType())) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.getName() == fieldName
      sut.getType() == field.getType()


    where:
      fieldName             || typeName                       | qualifiedTypeName
      "genericByteArray"    || "GenericType«Array«byte»»"     | "com.mangofactory.documentation.schema.GenericType<byte[]>"
      "genericCategoryArray"|| "GenericType«Array«Category»»" | "com.mangofactory.documentation.schema.GenericType<com.mangofactory.documentation.schema.Category[]>"
  }
}
