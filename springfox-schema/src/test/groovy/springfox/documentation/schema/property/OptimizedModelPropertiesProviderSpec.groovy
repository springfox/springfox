/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
package springfox.documentation.schema.property

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableSet
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import spock.lang.Specification
import springfox.documentation.schema.Category
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.TypeWithJsonFormat
import springfox.documentation.schema.TypeWithSetterButNoGetter
import springfox.documentation.schema.UnwrappedType
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.schema.property.bean.AccessorsProvider
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.TypeNameProviderPlugin

import static com.google.common.collect.Lists.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin(SchemaPluginsSupport)
class OptimizedModelPropertiesProviderSpec extends Specification {
  def "model properties are detected correctly"() {
    given:
    TypeResolver typeResolver = new TypeResolver()
    BeanPropertyNamingStrategy namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(
        typeResolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    OptimizedModelPropertiesProvider sut = new OptimizedModelPropertiesProvider(
        new AccessorsProvider(typeResolver),
        new FieldProvider(typeResolver),
        new FactoryMethodProvider(typeResolver),
        typeResolver,
        namingStrategy,
        defaultSchemaPlugins(),
        typeNameExtractor)
    ResolvedType type = typeResolver.resolve(TypeWithSetterButNoGetter)

    and:
    def objectMapperConfigured = new ObjectMapperConfigured(this, new ObjectMapper())
    namingStrategy.onApplicationEvent(objectMapperConfigured)
    sut.onApplicationEvent(objectMapperConfigured)

    when:
    def inputValue = sut.propertiesFor(
        type,
        inputParam("group",
            type,
            SPRING_WEB,
            new AlternateTypeProvider(newArrayList()),
            new DefaultGenericTypeNamingStrategy(),
            ImmutableSet.builder().build()))
    def returnValue = sut.propertiesFor(
        type,
        returnValue("group",
            type,
            SPRING_WEB,
            new AlternateTypeProvider(newArrayList()),
            new DefaultGenericTypeNamingStrategy(),
            ImmutableSet.builder().build()))

    then:
    inputValue.collect { it.name }.containsAll(['property'])
    returnValue.collect { it.name }.containsAll(['property'])
  }

  def "model unwrapped properties are detected correctly"() {
    given:
    TypeResolver typeResolver = new TypeResolver()
    BeanPropertyNamingStrategy namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(
        typeResolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    OptimizedModelPropertiesProvider sut = new OptimizedModelPropertiesProvider(
        new AccessorsProvider(typeResolver),
        new FieldProvider(typeResolver),
        new FactoryMethodProvider(typeResolver),
        typeResolver,
        namingStrategy,
        defaultSchemaPlugins(),
        typeNameExtractor)
    ResolvedType type = typeResolver.resolve(UnwrappedType)

    and:
    def objectMapperConfigured = new ObjectMapperConfigured(
        this,
        new ObjectMapper())
    namingStrategy.onApplicationEvent(objectMapperConfigured)
    sut.onApplicationEvent(objectMapperConfigured)

    when:
    def inputValue = sut.propertiesFor(
        type,
        inputParam("group",
            type,
            SPRING_WEB,
            new AlternateTypeProvider(newArrayList()),
            new DefaultGenericTypeNamingStrategy(),
            ImmutableSet.builder().build()))
    def returnValue = sut.propertiesFor(
        type,
        returnValue("group",
            type,
            SPRING_WEB,
            new AlternateTypeProvider(newArrayList()),
            new DefaultGenericTypeNamingStrategy(),
            ImmutableSet.builder().build()))

    then:
    inputValue.collect { it.name }.containsAll(['name'])
    returnValue.collect { it.name }.containsAll(['name'])
  }

  def "model ignored properties are detected correctly"() {
    given:
    TypeResolver typeResolver = new TypeResolver()
    BeanPropertyNamingStrategy namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create(
            [new DefaultTypeNameProvider()])
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(
        typeResolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    OptimizedModelPropertiesProvider sut = new OptimizedModelPropertiesProvider(
        new AccessorsProvider(typeResolver),
        new FieldProvider(typeResolver),
        new FactoryMethodProvider(typeResolver),
        typeResolver,
        namingStrategy,
        defaultSchemaPlugins(),
        typeNameExtractor)
    ResolvedType type = typeResolver.resolve(UnwrappedType)

    and:
    def objectMapperConfigured = new ObjectMapperConfigured(
        this,
        new ObjectMapper())
    namingStrategy.onApplicationEvent(objectMapperConfigured)
    sut.onApplicationEvent(objectMapperConfigured)

    and:
    def inputContext = inputParam("group",
        type,
        SPRING_WEB,
        new AlternateTypeProvider(newArrayList()),
        new DefaultGenericTypeNamingStrategy(),
        ImmutableSet.builder().build())
    def returnContext = returnValue("group",
        type,
        SPRING_WEB,
        new AlternateTypeProvider(newArrayList()),
        new DefaultGenericTypeNamingStrategy(),
        ImmutableSet.builder().build())

    when:
    inputContext.seen(typeResolver.resolve(Category))
    returnContext.seen(typeResolver.resolve(Category))

    and:
    def inputValue = sut.propertiesFor(type, inputContext)
    def returnValue = sut.propertiesFor(type, returnContext)

    then:
    inputValue.collect { it.name }.containsAll([])
    returnValue.collect { it.name }.containsAll([])
  }

  def "model JsonFormat properties are detected correctly"() {
    given:
    TypeResolver typeResolver = new TypeResolver()
    BeanPropertyNamingStrategy namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(
        typeResolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    OptimizedModelPropertiesProvider sut = new OptimizedModelPropertiesProvider(
        new AccessorsProvider(typeResolver),
        new FieldProvider(typeResolver),
        new FactoryMethodProvider(typeResolver),
        typeResolver,
        namingStrategy,
        defaultSchemaPlugins(),
        typeNameExtractor)
    ResolvedType type = typeResolver.resolve(TypeWithJsonFormat)

    and:
    def objectMapperConfigured = new ObjectMapperConfigured(
        this,
        new ObjectMapper())
    namingStrategy.onApplicationEvent(objectMapperConfigured)
    sut.onApplicationEvent(objectMapperConfigured)

    when:
    def inputValue = sut.propertiesFor(
        type,
        inputParam("group",
            type,
            SPRING_WEB,
            new AlternateTypeProvider(newArrayList()),
            new DefaultGenericTypeNamingStrategy(),
            ImmutableSet.builder().build()))
    def returnValue = sut.propertiesFor(
        type,
        returnValue("group",
            type,
            SPRING_WEB,
            new AlternateTypeProvider(newArrayList()),
            new DefaultGenericTypeNamingStrategy(),
            ImmutableSet.builder().build()))

    then:
    def inputProp = inputValue.find({ it.name == "localDate" })
    inputProp.type.erasedType.equals(String.class)
    inputProp.example.equals("MM-dd-yyyy")
    def returnProp = returnValue.find({ it.name == "localDate" })
    returnProp.type.erasedType.equals(String.class)
    returnProp.example.equals("MM-dd-yyyy")
  }
}
