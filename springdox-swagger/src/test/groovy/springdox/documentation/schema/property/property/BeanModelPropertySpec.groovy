package springdox.documentation.schema.property.property

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Ignore
import springdox.documentation.schema.*
import springdox.documentation.schema.configuration.ObjectMapperConfigured
import springdox.documentation.schema.mixins.ModelPropertyLookupSupport
import springdox.documentation.schema.mixins.TypesForTestingSupport
import springdox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springdox.documentation.schema.property.bean.BeanModelProperty
import springdox.documentation.service.AllowableListValues

import static com.google.common.collect.Lists.*
import static springdox.documentation.schema.property.BeanPropertyDefinitions.*
import static springdox.documentation.schema.property.bean.Accessors.*
import static springdox.documentation.spi.DocumentationType.*
import static springdox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, AlternateTypesSupport])
class BeanModelPropertySpec extends SchemaSpecification {

  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  def "Extracting information from resolved properties"() {
    given:
      Class typeToTest = TypeWithGettersAndSetters
      def modelContext = inputParam(typeToTest, SWAGGER_12, alternateTypeProvider(), namingStrategy)
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

      ObjectMapper mapper = new ObjectMapper()
      def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
      namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
      String propName = name(propertyDefinition, true, namingStrategy)
      def sut = new BeanModelProperty(propName, method, isGetter(method.getRawMember()),
              new TypeResolver(), alternateTypeProvider())


    expect:
      sut.propertyDescription() == null
      !sut.required
      typeNameExtractor.typeName(fromParent(modelContext, sut.getType())) == typeName
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
      Class typeToTest = TypeWithAnnotatedGettersAndSetters
      def modelContext = inputParam(typeToTest, SWAGGER_12, alternateTypeProvider(), namingStrategy)
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

      ObjectMapper mapper = new ObjectMapper()
      def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
      namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
      String propName = name(propertyDefinition, true, namingStrategy)
      def sut = new BeanModelProperty(propName, method, isGetter(method.getRawMember()),
              new TypeResolver(), alternateTypeProvider())

    expect:
      sut.propertyDescription() == description
      sut.required == required
      typeNameExtractor.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName

      if (allowableValues) {
        sut.allowableValues().getValues() == allowableValues.getValues()
        sut.allowableValues().getValueType() == allowableValues.getValueType()
      }
    
    where:
      methodName    | description              | required | allowableValues                                             | typeName  | qualifiedTypeName
      "getIntProp"  | "int Property Field"     | true     | null                                                        | "int"     | "int"
      "isBoolProp"  | "bool Property Getter"   | false    | null                                                        | "boolean" | "boolean"
      "getEnumProp" | "enum Prop Getter value" | true     | new AllowableListValues(newArrayList("ONE", "TWO"), "LIST") | "string"  | "springdox.documentation.schema.ExampleEnum"
      "setIntProp"  | "int Property Field"     | true     | null                                                        | "int"     | "int"
      "setBoolProp" | "bool Property Getter"   | false    | null                                                        | "boolean" | "boolean"
      "setEnumProp" | "enum Prop Getter value" | true     | new AllowableListValues(newArrayList("ONE", "TWO"), "LIST") | "string"  | "springdox.documentation.schema.ExampleEnum"
  }

  def "Respects JsonGetter annotations"() {

    given:
      Class typeToTest = typeForTestingJsonGetterAnnotation()
      def modelContext = inputParam(typeToTest, SWAGGER_12, alternateTypeProvider(), namingStrategy)
      def method = accessorMethod(typeToTest, methodName)
      def propertyDefinition = beanPropertyDefinition(typeToTest, methodName)

      ObjectMapper mapper = new ObjectMapper()
      def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
      namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
      String propName = name(propertyDefinition, true, namingStrategy)
      def sut = new BeanModelProperty(propName, method, isGetter(method.getRawMember()),
              new TypeResolver(), alternateTypeProvider())

    expect:
      typeNameExtractor.typeName(fromParent(modelContext, sut.getType())) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.allowableValues() == null

    where:
      methodName || typeName | qualifiedTypeName
      "value1"   || "string" | "java.lang.String"
  }
}
