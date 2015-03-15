package springdox.documentation.schema.property.constructor

import com.fasterxml.jackson.databind.ObjectMapper
import springdox.documentation.schema.AlternateTypesSupport
import springdox.documentation.schema.SchemaSpecification
import springdox.documentation.schema.configuration.ObjectMapperConfigured
import springdox.documentation.schema.mixins.ModelPropertyLookupSupport
import springdox.documentation.schema.mixins.TypesForTestingSupport
import springdox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springdox.documentation.schema.property.field.FieldModelProperty
import springdox.documentation.service.AllowableListValues

import static com.google.common.collect.Lists.*
import static springdox.documentation.schema.property.BeanPropertyDefinitions.*
import static springdox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, AlternateTypesSupport])
class ConstructorModelPropertySpec extends SchemaSpecification {

  def "Extracting information from resolved constructor params" () {
    given:
      def typeToTest = typeWithConstructorProperty()
      def beanPropertyDefinition = beanPropertyDefinitionByField(typeToTest, fieldName)
      def modelContext = inputParam(typeToTest, documentationType, alternateTypeProvider())
      def field = field(typeToTest, fieldName)
      ObjectMapper mapper = new ObjectMapper()
      def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
      namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
      String propName = name(beanPropertyDefinition, true,  namingStrategy)
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
    "foobar"              || null         | false      | "string"             | "springdox.documentation.schema.TypeWithConstructorProperty\$Foobar" | ["Foo", "Bar"]
    "visibleForSerialize" || null         | false      | "long"               | "java.lang.Long"                                                      | null
  }
}
