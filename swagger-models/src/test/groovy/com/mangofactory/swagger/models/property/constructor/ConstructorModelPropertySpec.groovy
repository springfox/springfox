package com.mangofactory.swagger.models.property.constructor

import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.mixins.ModelPropertyLookupSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.ModelContext
import com.mangofactory.swagger.models.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.dto.AllowableListValues
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList
import static com.mangofactory.swagger.models.property.BeanPropertyDefinitions.name

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport])
class ConstructorModelPropertySpec extends Specification {
  def "Extracting information from resolved constructor params" () {
    given:
      def typeToTest = typeWithConstructorProperty()
      def beanPropertyDefinition = beanPropertyDefinitionByField(typeToTest, fieldName)
      def modelContext = ModelContext.inputParam(typeToTest )
      def field = field(typeToTest, fieldName)
      ObjectMapper mapper = new ObjectMapper()
      String propName = name(beanPropertyDefinition, true,  new ObjectMapperBeanPropertyNamingStrategy(mapper))
      def sut = new com.mangofactory.swagger.models.property.field.FieldModelProperty(propName, field,
              new AlternateTypeProvider())

    expect:
      sut.propertyDescription() == description
      sut.required == isRequired
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      if (allowableValues != null) {
        sut.allowableValues() == new AllowableListValues(newArrayList(allowableValues), "string")
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
