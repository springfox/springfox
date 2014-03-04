package com.mangofactory.swagger.models
import com.fasterxml.classmate.MemberResolver
import com.fasterxml.classmate.ResolvedTypeWithMembers
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import com.mangofactory.swagger.mixins.ModelPropertySupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.model.AllowableListValues
import scala.collection.JavaConversions
import spock.lang.Specification

import static com.google.common.collect.Lists.*
import static com.mangofactory.swagger.models.ResolvedTypes.*
import static com.mangofactory.swagger.models.ScalaConverters.*

@Mixin([TypesForTestingSupport, ModelPropertySupport])
class FieldModelPropertySpec extends Specification {
  def "Extracting information from resolved fields" () {
    given:
      def typeToTest = typeForTestingGettersAndSetters()
      def modelContext = ModelContext.inputParam(typeToTest )
      def field = field(typeToTest, fieldName)
      def sut = new FieldModelProperty(fieldName, field)

    expect:
      fromOption(sut.propertyDescription()) == description
      sut.required == isRequired
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      if (allowableValues != null) {
        def values = JavaConversions.collectionAsScalaIterable(newArrayList(allowableValues)).toList()
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
}
