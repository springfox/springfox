package springfox.documentation.schema.property.bean

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import springfox.documentation.schema.AlternateTypesSupport
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.mixins.ModelPropertyLookupSupport
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springfox.documentation.spi.DocumentationType

import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, ModelProviderSupport, SchemaPluginsSupport, AlternateTypesSupport])
class BeanModelPropertyProviderSpec extends Specification {


  def "Respect property ordering" () {

    given:
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
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
              returnValue(resolvedType, DocumentationType.SWAGGER_12, alternateTypeProvider(), genericNamingStrategy))
              .collect({it.name})
      def deSerializationPropNames = beanModelPropertyProvider.propertiesFor(resolvedType,
              inputParam(resolvedType, DocumentationType.SWAGGER_12, alternateTypeProvider(), genericNamingStrategy))
              .collect({it.name})

    expect:
      serializationPropNames == ['name', 'age', 'category', 'customType']
      deSerializationPropNames == ['name', 'age', 'category', 'customType']


  }

}
