package com.mangofactory.schema.property.constructor
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.schema.SchemaSpecification
import com.mangofactory.schema.alternates.AlternateTypeProvider
import com.mangofactory.schema.property.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.schema.property.field.FieldModelProperty
import com.mangofactory.service.model.AllowableListValues
import com.mangofactory.swagger.mixins.ModelPropertyLookupSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport

import static com.google.common.collect.Lists.*
import static com.mangofactory.schema.plugins.ModelContext.*
import static com.mangofactory.schema.property.BeanPropertyDefinitions.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport])
class ConstructorModelPropertySpec extends SchemaSpecification {

  def "Extracting information from resolved constructor params" () {
    given:
      def typeToTest = typeWithConstructorProperty()
      def beanPropertyDefinition = beanPropertyDefinitionByField(typeToTest, fieldName)
      def modelContext = inputParam(typeToTest, documentationType)
      def field = field(typeToTest, fieldName)
      ObjectMapper mapper = new ObjectMapper()
      String propName = name(beanPropertyDefinition, true,  new ObjectMapperBeanPropertyNamingStrategy(mapper))
      def sut = new FieldModelProperty(propName, field, new AlternateTypeProvider())

    expect:
      sut.propertyDescription() == description
      sut.required == isRequired
      typeNameExtractor.typeName(fromParent(modelContext, sut.getType())) == typeName
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
    "foobar"              || null         | false      | "string"             | "com.mangofactory.schema.TypeWithConstructorProperty\$Foobar" | ["Foo", "Bar"]
    "visibleForSerialize" || null         | false      | "long"               | "java.lang.Long"                                                      | null
  }
}
