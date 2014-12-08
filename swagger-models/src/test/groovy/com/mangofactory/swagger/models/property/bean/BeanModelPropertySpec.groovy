package com.mangofactory.swagger.models.property.bean

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.mixins.ModelPropertyLookupSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.ModelContext
import com.mangofactory.swagger.models.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.dto.AllowableListValues
import spock.lang.Specification
import spock.lang.Unroll

import static com.google.common.collect.Lists.newArrayList
import static com.mangofactory.swagger.models.property.BeanPropertyDefinitions.name
import static com.mangofactory.swagger.models.property.bean.Accessors.isGetter

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport])
class BeanModelPropertySpec extends Specification {


  def "Extracting information from resolved properties"() {

    given:
      Class typeToTest = typeForTestingGettersAndSetters()
      def modelContext = ModelContext.inputParam(typeToTest)
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

      ObjectMapper mapper = new ObjectMapper()
      String propName = name(propertyDefinition, true, new ObjectMapperBeanPropertyNamingStrategy(mapper))
      def sut = new BeanModelProperty(propName, propertyDefinition, method, isGetter(method.getRawMember()),
              new TypeResolver(), new AlternateTypeProvider())


    expect:
      sut.propertyDescription() == description
      sut.required == required
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.allowableValues() == null


    where:
      methodName    | description            | required | typeName  | qualifiedTypeName
      "getIntProp"  | "int Property Field"   | true     | "int"     | "int"
      "isBoolProp"  | "bool Property Getter" | false    | "boolean" | "boolean"
      "setIntProp"  | "int Property Field"   | true     | "int"     | "int"
      "setBoolProp" | "bool Property Getter" | false    | "boolean" | "boolean"
  }

  @Unroll
  def "Extracting information from ApiModelProperty annotation"() {
    given:
      Class typeToTest = typeForTestingAnnotatedGettersAndSetter()
      def modelContext = ModelContext.inputParam(typeToTest)
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

      ObjectMapper mapper = new ObjectMapper()
      String propName = name(propertyDefinition, true, new ObjectMapperBeanPropertyNamingStrategy(mapper))
      def sut = new BeanModelProperty(propName, propertyDefinition, method, isGetter(method.getRawMember()),
              new TypeResolver(), new AlternateTypeProvider())

    expect:
      sut.propertyDescription() == description
      sut.required == required
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName

      if (sut.allowableValues()) {
        sut.allowableValues().getValues() == allowableValues.getValues()
        sut.allowableValues().getValueType() == allowableValues.getValueType()
      }
    
    where:
      methodName    | description              | required | allowableValues                                             | typeName  | qualifiedTypeName
      "getIntProp"  | "int Property Field"     | true     | null                                                        | "int"     | "int"
      "isBoolProp"  | "bool Property Getter"   | false    | null                                                        | "boolean" | "boolean"
      "getEnumProp" | "enum Prop Getter value" | true     | new AllowableListValues(newArrayList("ONE", "TWO"), "LIST") | "string"  | "com.mangofactory.swagger.models.ExampleEnum"
      "setIntProp"  | "int Property Field"     | true     | null                                                        | "int"     | "int"
      "setBoolProp" | "bool Property Getter"   | false    | null                                                        | "boolean" | "boolean"
      "setEnumProp" | "enum Prop Getter value" | true     | new AllowableListValues(newArrayList("ONE", "TWO"), "LIST") | "string"  | "com.mangofactory.swagger.models.ExampleEnum"
  }


  def "Respects JsonGetter annotations"() {

    given:
      Class typeToTest = typeForTestingJsonGetterAnnotation()
      def modelContext = ModelContext.inputParam(typeToTest)
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

      ObjectMapper mapper = new ObjectMapper()
      String propName = name(propertyDefinition, true, new ObjectMapperBeanPropertyNamingStrategy(mapper))
      def sut = new BeanModelProperty(propName, propertyDefinition, method, isGetter(method.getRawMember()),
              new TypeResolver(), new AlternateTypeProvider())

    expect:
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.allowableValues() == null


    where:
      methodName || typeName | qualifiedTypeName
      "value1"   || "string" | "java.lang.String"
  }
}
