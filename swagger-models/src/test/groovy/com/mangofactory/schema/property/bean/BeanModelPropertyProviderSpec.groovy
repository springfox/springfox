package com.mangofactory.schema.property.bean
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.schema.TypeNameExtractor
import com.mangofactory.schema.alternates.AlternateTypeProvider
import com.mangofactory.schema.plugins.DocumentationType
import com.mangofactory.schema.property.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.swagger.mixins.ModelPropertyLookupSupport
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Specification

import static com.mangofactory.schema.plugins.ModelContext.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, ModelProviderSupport])
class BeanModelPropertyProviderSpec extends Specification {


  def "Respect property ordering" () {

    given:
      Class typeToTest = complexType()
      def typeResolver = new TypeResolver()
      ResolvedType resolvedType = typeResolver.resolve(typeToTest)
      ObjectMapper mapper = new ObjectMapper()

      def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver,
              new AlternateTypeProvider(), new ObjectMapperBeanPropertyNamingStrategy(mapper), pluginsManager(),
              Mock(TypeNameExtractor))
      beanModelPropertyProvider.objectMapper = mapper
      def serializationPropNames = beanModelPropertyProvider.propertiesFor(resolvedType,
              returnValue(resolvedType, DocumentationType.SWAGGER_12))
              .collect({it.name})
      def deSerializationPropNames = beanModelPropertyProvider.propertiesFor(resolvedType,
              inputParam(resolvedType, DocumentationType.SWAGGER_12)).collect({it.name})

    expect:
      serializationPropNames == ['name', 'age', 'category', 'customType']
      deSerializationPropNames == ['name', 'age', 'category', 'customType']


  }

}
