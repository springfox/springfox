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

import com.google.common.base.Function
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.fasterxml.classmate.TypeResolver
import com.google.common.collect.ImmutableSet
import org.springframework.http.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport, AlternateTypesSupport])
class ModelProviderSpec extends Specification {

  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  def uniqueTypeNameAdjuster = new TypeNameIndexingAdapter();
  def getNames = 
      new Function<Model, String>() {
        public String apply(Model model) {
          return model.getName();
        }}
  def "dependencies provider respects ignorables"() {
    given:
      ModelProvider sut = defaultModelProvider()
      def context = inputParam(
          "group",
          modelType,
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          uniqueTypeNameAdjuster,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      context.seen(new TypeResolver().resolve(HttpHeaders))
      def dependentTypeNames = Maps.uniqueIndex(sut.dependencies(context).values(), getNames)
          .keySet().sort()

    expect:
      dependencies == dependentTypeNames

    where:
      modelType                      | dependencies
      genericClassWithGenericField() | ["ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()
  }

  @Unroll
  def "dependencies are inferred correctly by the model provider"() {
    given:
      ModelProvider provider = defaultModelProvider()
      def dependentTypeNames = Maps.uniqueIndex(provider.dependencies(
        inputParam(
            "group",
            resolver.resolve(modelType),
            Optional.absent(),
            new HashSet<>(),
            SWAGGER_12,
            uniqueTypeNameAdjuster,
            alternateTypeProvider(),
            namingStrategy,
            ImmutableSet.builder().build())).values(), getNames)
        .keySet().sort()

    expect:
      dependencies == dependentTypeNames

    where:
      modelType                      | dependencies
      simpleType()                   | []
      complexType()                  | ["Category"]
      inheritedComplexType()         | ["Category"]
      typeWithLists()                | ["Category", "ComplexType", "Substituted"].sort()
      typeWithSets()                 | ["Category", "ComplexType"].sort()
      typeWithArrays()               | ["Category", "ComplexType", "Substituted"]
      genericClass()                 | ["SimpleType"]
      genericClassWithListField()    | ["SimpleType"]
      genericClassWithGenericField() | ["ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()
      genericClassWithDeepGenerics() | ["ResponseEntityAlternative«List«SimpleType»»", "SimpleType"].sort()
      genericCollectionWithEnum()    | []
      recursiveType()                | ["SimpleType"]
  }
}
