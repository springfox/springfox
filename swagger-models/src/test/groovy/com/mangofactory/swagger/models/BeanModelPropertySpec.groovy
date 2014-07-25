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
      def sut = new BeanModelProperty(methodName, description, method, Accessors.isGetter(method.getRawMember()),
              new TypeResolver(), new AlternateTypeProvider())

    expect:
      fromOption(sut.propertyDescription()) == description
      sut.required == false
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.allowableValues() == null


    where:
    methodName      || description            | typeName     | qualifiedTypeName
    "getIntProp"    || null                   | "int"        | "int"
    "isBoolProp"    || "bool Property Getter" | "boolean"    | "boolean"
    "setIntProp"    || null                   | "int"        | "int"
    "setBoolProp"   || null                   | "boolean"    | "boolean"
  }
}
