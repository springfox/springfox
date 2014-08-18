package com.mangofactory.swagger.models.property.constructor

import com.mangofactory.swagger.mixins.ModelPropertySupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.BeanPropertyNamingStrategy
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.ModelContext
import com.mangofactory.swagger.models.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.property.BeanPropertyDefinitions
import com.wordnik.swagger.model.AllowableListValues
import scala.collection.JavaConversions
import spock.lang.Specification
import spock.lang.Unroll

import static com.google.common.collect.Lists.newArrayList
import static com.mangofactory.swagger.models.ScalaConverters.fromOption
import static com.mangofactory.swagger.models.property.BeanPropertyDefinitions.name

@Mixin([TypesForTestingSupport, ModelPropertySupport])
class ConstructorModelPropertySpec extends Specification {
  def "Extracting information from resolved constructor params" () {
    given:
      def typeToTest = typeWithConstructorProperty()
      def beanPropertyDefinition = ModelPropertySupport.beanPropertyDefinitionByField(typeToTest, fieldName)
      def modelContext = ModelContext.inputParam(typeToTest )
      def field = field(typeToTest, fieldName)
      ObjectMapper mapper = new ObjectMapper()
      String propName = name(beanPropertyDefinition, true,  new ObjectMapperBeanPropertyNamingStrategy(mapper))
      def sut = new com.mangofactory.swagger.models.property.field.FieldModelProperty(propName, field,
              new AlternateTypeProvider())

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
    fieldName             || description  | isRequired | typeName             | qualifiedTypeName                                                     | allowableValues
    "foobar"              || null         | false      | "string"             | "com.mangofactory.swagger.models.TypeWithConstructorProperty\$Foobar" | ["Foo", "Bar"]
    "visibleForSerialize" || null         | false      | "long"               | "java.lang.Long"                                                      | null
  }
}
