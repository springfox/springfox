package com.mangofactory.swagger.models

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.mixins.ModelPropertySupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import spock.lang.Specification

import static com.mangofactory.swagger.models.ScalaConverters.*

@Mixin([TypesForTestingSupport, ModelPropertySupport])
class BeanModelPropertySpec extends Specification {
  def "Extracting information from resolved properties" () {
    given:
      Class typeToTest = typeForTestingGettersAndSetters()
      def modelContext = ModelContext.inputParam(typeToTest)
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)
      def sut = new BeanModelProperty(propertyDefinition, method, Accessors.isGetter(method.getRawMember()),
              new TypeResolver(), new AlternateTypeProvider())

    expect:
      fromOption(sut.propertyDescription()) == description
      sut.required == required
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.allowableValues() == null


    where:
    methodName      || description            | required  | typeName     | qualifiedTypeName
    "getIntProp"    || "int Property Field"   | true      | "int"        | "int"
    "isBoolProp"    || "bool Property Getter" | false     | "boolean"    | "boolean"
    "setIntProp"    || "int Property Field"   | true      | "int"        | "int"
    "setBoolProp"   || "bool Property Getter" | false     | "boolean"    | "boolean"
  }
}
