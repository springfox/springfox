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
package springfox.documentation.schema

import com.fasterxml.classmate.TypeResolver
import com.google.common.collect.ImmutableSet
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import spock.lang.Specification
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin

import static springfox.documentation.schema.ResolvedTypes.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin(AlternateTypesSupport)
class ModelReferenceProviderSpec extends Specification {
  def "Map of Maps is rendered correctly" () {
    given:
      def modelContext = inputParam(
          "group",
          TypeWithMapOfMaps,
          DocumentationType.SWAGGER_2,
          alternateTypeProvider(),
          new DefaultGenericTypeNamingStrategy(),
          ImmutableSet.builder().build())
      def resolver = new TypeResolver()
      def typeNameExtractor = aTypeNameExtractor(resolver)
    when:
      def sut = modelRefFactory(modelContext, typeNameExtractor)
          .apply(resolver.resolve(
            Map,
            resolver.resolve(String),
            resolver.resolve(Map, String, Foo)))
    then:
      //TODO: Elaborate this test
      sut.itemModel().isPresent()
  }

  def aTypeNameExtractor(TypeResolver resolver) {
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    def typeNameExtractor = new TypeNameExtractor(
        resolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    typeNameExtractor
  }


  class TypeWithMapOfMaps {
    public Map<String, Map<String, Foo>> innerMap;
  }

  class Foo {
    public Integer fooInt;
  }

}
