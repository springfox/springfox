package com.mangofactory.schema.property.bean

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.schema.plugins.ModelContext
import com.mangofactory.schema.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.schema.alternates.AlternateTypeProvider
import com.mangofactory.service.model.AllowableListValues
import com.mangofactory.swagger.mixins.ModelPropertyLookupSupport
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Ignore
import spock.lang.Specification

import static com.google.common.collect.Lists.*
import static com.mangofactory.schema.property.BeanPropertyDefinitions.*
import static com.mangofactory.schema.property.bean.Accessors.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, ModelProviderSupport])
class BeanModelPropertySpec extends Specification {

  def "Extracting information from resolved properties"() {

    given:
      Class typeToTest = typeForTestingGettersAndSetters()
      def modelContext = ModelContext.inputParam(typeToTest, documentationType())
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

      ObjectMapper mapper = new ObjectMapper()
      String propName = name(propertyDefinition, true, new ObjectMapperBeanPropertyNamingStrategy(mapper))
      def sut = new BeanModelProperty(propName, method, isGetter(method.getRawMember()),
              new TypeResolver(), new AlternateTypeProvider())


    expect:
      sut.propertyDescription() == null
      !sut.required
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.allowableValues() == null


    where:
      methodName    |  required | typeName  | qualifiedTypeName
      "getIntProp"  |  true     | "int"     | "int"
      "isBoolProp"  |  false    | "boolean" | "boolean"
      "setIntProp"  |  true     | "int"     | "int"
      "setBoolProp" |  false    | "boolean" | "boolean"
  }

  @Ignore("Fix this via the plugin manager")
  def "Extracting information from ApiModelProperty annotation"() {
    given:
      Class typeToTest = typeForTestingAnnotatedGettersAndSetter()
      def modelContext = ModelContext.inputParam(typeToTest, documentationType())
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

      ObjectMapper mapper = new ObjectMapper()
      String propName = name(propertyDefinition, true, new ObjectMapperBeanPropertyNamingStrategy(mapper))
      def sut = new BeanModelProperty(propName, method, isGetter(method.getRawMember()),
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
      "getEnumProp" | "enum Prop Getter value" | true     | new AllowableListValues(newArrayList("ONE", "TWO"), "LIST") | "string"  | "com.mangofactory.schema.ExampleEnum"
      "setIntProp"  | "int Property Field"     | true     | null                                                        | "int"     | "int"
      "setBoolProp" | "bool Property Getter"   | false    | null                                                        | "boolean" | "boolean"
      "setEnumProp" | "enum Prop Getter value" | true     | new AllowableListValues(newArrayList("ONE", "TWO"), "LIST") | "string"  | "com.mangofactory.schema.ExampleEnum"
  }


  def "Respects JsonGetter annotations"() {

    given:
      Class typeToTest = typeForTestingJsonGetterAnnotation()
      def modelContext = ModelContext.inputParam(typeToTest, documentationType())
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

      ObjectMapper mapper = new ObjectMapper()
      String propName = name(propertyDefinition, true, new ObjectMapperBeanPropertyNamingStrategy(mapper))
      def sut = new BeanModelProperty(propName, method, isGetter(method.getRawMember()),
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
