/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.schema.property.field
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import springfox.documentation.schema.AlternateTypesSupport
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ExampleEnum
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.mixins.ConfiguredObjectMapperSupport
import springfox.documentation.schema.mixins.ModelPropertyLookupSupport
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springfox.documentation.spi.DocumentationType

import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, ModelProviderSupport, SchemaPluginsSupport,
    AlternateTypesSupport, ConfiguredObjectMapperSupport])
class FieldModelPropertyProviderSpec extends Specification {

  def "Respect property ordering" () {

    given:
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      Class typeToTest = complexType()
      def typeResolver = new TypeResolver()
      ResolvedType resolvedType = typeResolver.resolve(typeToTest)
      ObjectMapper mapper = objectMapperThatUsesFields()
      def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
      namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
      def propertyProvider = new FieldModelPropertyProvider(new FieldProvider(typeResolver),
              namingStrategy, defaultSchemaPlugins(), Mock(TypeNameExtractor))
      propertyProvider.objectMapper = mapper
      def serializationPropNames = propertyProvider.propertiesFor(resolvedType, returnValue(resolvedType, DocumentationType.SWAGGER_12, alternateTypeProvider(), genericNamingStrategy)
      )
              .collect({it.name})
      def deSerializationPropNames = propertyProvider.propertiesFor(resolvedType, inputParam(resolvedType, DocumentationType.SWAGGER_12, alternateTypeProvider(), genericNamingStrategy)
      )
              .collect({it.name})

    expect:
      serializationPropNames == ['name', 'age', 'category', 'customType']
      deSerializationPropNames == ['name', 'age', 'category', 'customType']

  }

  def "Handles unwrapped fields" () {

    given:
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      Class typeToTest = unwrappedType()
      def typeResolver = new TypeResolver()
      ResolvedType resolvedType = typeResolver.resolve(typeToTest)
      ObjectMapper mapper = objectMapperThatUsesFields()
      def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
      namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
      def propertyProvider = new FieldModelPropertyProvider(new FieldProvider(typeResolver),
          namingStrategy, defaultSchemaPlugins(), Mock(TypeNameExtractor))
      propertyProvider.objectMapper = mapper
      def serializationPropNames = propertyProvider.propertiesFor(resolvedType, returnValue(resolvedType, DocumentationType.SWAGGER_12, alternateTypeProvider(), genericNamingStrategy)
      )
          .collect({it.name})
      def deSerializationPropNames = propertyProvider.propertiesFor(resolvedType, inputParam(resolvedType, DocumentationType.SWAGGER_12, alternateTypeProvider(), genericNamingStrategy)
      )
          .collect({it.name})

    expect:
      serializationPropNames == ['name']
      deSerializationPropNames == ['name']

  }

  def "Handles enum collections" () {

    given:
    def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
    Class typeToTest = collectionEnumType()
    def typeResolver = new TypeResolver()
    ResolvedType resolvedType = typeResolver.resolve(typeToTest)
    ObjectMapper mapper = objectMapperThatUsesFields()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
    def propertyProvider = new FieldModelPropertyProvider(new FieldProvider(typeResolver),
            namingStrategy, defaultSchemaPlugins(), Mock(TypeNameExtractor))
    propertyProvider.objectMapper = mapper

    def serializationProps =
            propertyProvider.propertiesFor(resolvedType, returnValue(resolvedType, DocumentationType.SWAGGER_12, alternateTypeProvider(), genericNamingStrategy))

    def deSerializationProps =
            propertyProvider.propertiesFor(resolvedType, inputParam(resolvedType, DocumentationType.SWAGGER_12, alternateTypeProvider(), genericNamingStrategy))


    expect:
    def assertModelProperties = { props ->
      assert props.size() == 1
      assert props.first().name == 'exampleEnums'
      assert props.first().type.erasedType == List
      assert props.first().type.typeParameters.size() == 1
      assert props.first().type.typeParameters.first().erasedType == ExampleEnum
      assert props.first().allowableValues.values == ['ONE', 'TWO']
      true
    }

    assertModelProperties(serializationProps)
    assertModelProperties(deSerializationProps)
  }

}
