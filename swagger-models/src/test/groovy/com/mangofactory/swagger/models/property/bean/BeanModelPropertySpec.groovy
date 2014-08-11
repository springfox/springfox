package com.mangofactory.swagger.models.property.bean
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.mixins.ModelPropertySupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.ModelContext
import com.mangofactory.swagger.models.NoRenamingStrategy
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.wordnik.swagger.model.AllowableListValues
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList
import static com.mangofactory.swagger.models.ScalaConverters.fromOption
import static scala.collection.JavaConversions.collectionAsScalaIterable

@Mixin([TypesForTestingSupport, ModelPropertySupport])
class BeanModelPropertySpec extends Specification {

  // @formatter:off
  def "Extracting information from resolved properties"() {

    given:
      Class typeToTest = typeForTestingGettersAndSetters()
      def modelContext = ModelContext.inputParam(typeToTest)
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)
      def sut = new BeanModelProperty(propertyDefinition, method, Accessors.isGetter(method.getRawMember()),
              new TypeResolver(), new AlternateTypeProvider(), new NoRenamingStrategy())


    expect:
      fromOption(sut.propertyDescription()) == description
      sut.required == required
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.allowableValues() == null


    where:
      methodName    || description              | required  | typeName  | qualifiedTypeName
      "getIntProp"  || "int Property Field"     | true      | "int"     | "int"
      "isBoolProp"  || "bool Property Getter"   | false     | "boolean" | "boolean"
      "setIntProp"  || "int Property Field"     | true      | "int"     | "int"
      "setBoolProp" || "bool Property Getter"   | false     | "boolean" | "boolean"
  }


  def "Extracting information from ApiModelProperty annotation"() {
    given:
      Class typeToTest = typeForTestingAnnotatedGettersAndSetter()
      def modelContext = ModelContext.inputParam(typeToTest)
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)
      def sut = new BeanModelProperty(propertyDefinition, method, Accessors.isGetter(method.getRawMember()),
              new TypeResolver(), new AlternateTypeProvider(), new NoRenamingStrategy())

    expect:
      fromOption(sut.propertyDescription()) == description
      sut.required == required
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.allowableValues() == allowableValues

    where:
      methodName    || description              | required  | allowableValues                                                                           | typeName  | qualifiedTypeName
      "getIntProp"  || "int Property Field"     | true      | null                                                                                      | "int"     | "int"
      "isBoolProp"  || "bool Property Getter"   | false     | null                                                                                      | "boolean" | "boolean"
      "getEnumProp" || "enum Prop Getter value" | true      | new AllowableListValues(collectionAsScalaIterable(newArrayList("ONE", "TWO")).toList(), "LIST")  | "string"  | "com.mangofactory.swagger.models.ExampleEnum"
      "setIntProp"  || "int Property Field"     | true      | null                                                                                      | "int"     | "int"
      "setBoolProp" || "bool Property Getter"   | false     | null                                                                                      | "boolean" | "boolean"
      "setEnumProp" || "enum Prop Getter value" | true      | new AllowableListValues(collectionAsScalaIterable(newArrayList("ONE", "TWO")).toList(), "LIST")  | "string"  | "com.mangofactory.swagger.models.ExampleEnum"
  }
  // @formatter:on
}
