package com.mangofactory.documentation.schema.property.constructor

import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.documentation.schema.AlternateTypesSupport
import com.mangofactory.documentation.schema.SchemaSpecification
import com.mangofactory.documentation.schema.mixins.ModelPropertyLookupSupport
import com.mangofactory.documentation.schema.mixins.TypesForTestingSupport
import com.mangofactory.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.documentation.schema.property.field.FieldModelProperty
import com.mangofactory.documentation.service.model.AllowableListValues

import static com.google.common.collect.Lists.*
import static com.mangofactory.documentation.schema.property.BeanPropertyDefinitions.*
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, AlternateTypesSupport])
class ConstructorModelPropertySpec extends SchemaSpecification {

  def "Extracting information from resolved constructor params" () {
    given:
      def typeToTest = typeWithConstructorProperty()
      def beanPropertyDefinition = beanPropertyDefinitionByField(typeToTest, fieldName)
      def modelContext = inputParam(typeToTest, documentationType, alternateTypeProvider())
      def field = field(typeToTest, fieldName)
      ObjectMapper mapper = new ObjectMapper()
      String propName = name(beanPropertyDefinition, true,  new ObjectMapperBeanPropertyNamingStrategy(mapper))
      def sut = new FieldModelProperty(propName, field, alternateTypeProvider())

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
    "foobar"              || null         | false      | "string"             | "com.mangofactory.documentation.schema.TypeWithConstructorProperty\$Foobar" | ["Foo", "Bar"]
    "visibleForSerialize" || null         | false      | "long"               | "java.lang.Long"                                                      | null
  }
}
