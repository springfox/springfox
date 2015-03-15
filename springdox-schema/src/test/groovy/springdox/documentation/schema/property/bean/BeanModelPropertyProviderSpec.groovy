package springdox.documentation.schema.property.bean

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import springdox.documentation.schema.AlternateTypesSupport
import springdox.documentation.schema.TypeNameExtractor
import springdox.documentation.schema.configuration.ObjectMapperConfigured
import springdox.documentation.schema.mixins.ModelPropertyLookupSupport
import springdox.documentation.schema.mixins.ModelProviderSupport
import springdox.documentation.schema.mixins.SchemaPluginsSupport
import springdox.documentation.schema.mixins.TypesForTestingSupport
import springdox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springdox.documentation.spi.DocumentationType

import static springdox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, ModelProviderSupport, SchemaPluginsSupport, AlternateTypesSupport])
class BeanModelPropertyProviderSpec extends Specification {


  def "Respect property ordering" () {

    given:
      Class typeToTest = complexType()
      def typeResolver = new TypeResolver()
      ResolvedType resolvedType = typeResolver.resolve(typeToTest)
      ObjectMapper mapper = new ObjectMapper()
      def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
      namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
      def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver
              , namingStrategy, defaultSchemaPlugins(),
              Mock(TypeNameExtractor))
      beanModelPropertyProvider.objectMapper = mapper
      def serializationPropNames = beanModelPropertyProvider.propertiesFor(resolvedType,
              returnValue(resolvedType, DocumentationType.SWAGGER_12, alternateTypeProvider()))
              .collect({it.name})
      def deSerializationPropNames = beanModelPropertyProvider.propertiesFor(resolvedType,
              inputParam(resolvedType, DocumentationType.SWAGGER_12, alternateTypeProvider())).collect({it.name})

    expect:
      serializationPropNames == ['name', 'age', 'category', 'customType']
      deSerializationPropNames == ['name', 'age', 'category', 'customType']


  }

}
