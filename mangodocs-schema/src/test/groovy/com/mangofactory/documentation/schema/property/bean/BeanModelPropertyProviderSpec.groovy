package com.mangofactory.documentation.schema.property.bean

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.documentation.schema.AlternateTypesSupport
import com.mangofactory.documentation.schema.TypeNameExtractor
import com.mangofactory.documentation.schema.mixins.ModelPropertyLookupSupport
import com.mangofactory.documentation.schema.mixins.ModelProviderSupport
import com.mangofactory.documentation.schema.mixins.SchemaPluginsSupport
import com.mangofactory.documentation.schema.mixins.TypesForTestingSupport
import com.mangofactory.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.documentation.spi.DocumentationType
import spock.lang.Specification

import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, ModelProviderSupport, SchemaPluginsSupport, AlternateTypesSupport])
class BeanModelPropertyProviderSpec extends Specification {


  def "Respect property ordering" () {

    given:
      Class typeToTest = complexType()
      def typeResolver = new TypeResolver()
      ResolvedType resolvedType = typeResolver.resolve(typeToTest)
      ObjectMapper mapper = new ObjectMapper()

      def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver
              , new ObjectMapperBeanPropertyNamingStrategy(mapper), defaultSchemaPlugins(),
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
