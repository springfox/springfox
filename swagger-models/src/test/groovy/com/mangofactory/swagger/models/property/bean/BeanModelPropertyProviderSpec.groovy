package com.mangofactory.swagger.models.property.bean

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.mixins.ModelPropertyLookupSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import spock.lang.Specification


@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport])
class BeanModelPropertyProviderSpec extends Specification {


  def "Respect property ordering" () {

    given:
      Class typeToTest = complexType()
      def typeResolver = new TypeResolver()
      ResolvedType resolvedType = typeResolver.resolve(typeToTest)
      ObjectMapper mapper = new ObjectMapper()

      def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver,
            new AlternateTypeProvider(), new ObjectMapperBeanPropertyNamingStrategy(mapper))
      beanModelPropertyProvider.objectMapper = mapper
      def serializationPropNames = beanModelPropertyProvider.propertiesForSerialization(resolvedType).collect({it.name})
      def deSerializationPropNames = beanModelPropertyProvider.propertiesForDeserialization(resolvedType).collect({it
              .name})

    expect:
      serializationPropNames == ['name', 'age', 'category', 'customType']
      deSerializationPropNames == ['name', 'age', 'category', 'customType']


  }

}
