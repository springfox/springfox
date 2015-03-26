package springfox.documentation.schema.property.constructor

import com.fasterxml.jackson.databind.ObjectMapper
import springfox.documentation.service.AllowableListValues
import springfox.documentation.schema.AlternateTypesSupport
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.SchemaSpecification
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.mixins.ModelPropertyLookupSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springfox.documentation.schema.property.field.FieldModelProperty

import static com.google.common.collect.Lists.*
import static springfox.documentation.schema.property.BeanPropertyDefinitions.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, AlternateTypesSupport])
class ConstructorModelPropertySpec extends SchemaSpecification {

  def "Extracting information from resolved constructor params" () {
    given:
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      def typeToTest = typeWithConstructorProperty()
      def beanPropertyDefinition = beanPropertyDefinitionByField(typeToTest, fieldName)
      def modelContext = inputParam(typeToTest, documentationType, alternateTypeProvider(), genericNamingStrategy)
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
    "foobar"              || null         | false      | "string"             | "springfox.documentation.schema.TypeWithConstructorProperty\$Foobar" | ["Foo", "Bar"]
    "visibleForSerialize" || null         | false      | "long"               | "java.lang.Long"                                                      | null
  }
}
