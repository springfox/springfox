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
import org.springframework.http.HttpHeaders
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.mixins.ModelProviderSupport

import java.util.function.Function
import java.util.stream.Collectors

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ModelProviderSpec extends Specification implements ModelProviderSupport {
  @Shared def resolver = new TypeResolver()
  @Shared def namingStrategy = new DefaultGenericTypeNamingStrategy()

  def getNames =
      new Function<Model, String>() {
        String apply(Model model) {
          return model.getName();
        }
      }

  def "dependencies provider respects ignorables"() {
    given:
    ModelProvider sut = defaultModelProvider()
    def context = inputParam(
        "0_0",
        "group",
        modelType,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    context.seen(new TypeResolver().resolve(HttpHeaders))
    def dependentTypeNames = sut.dependencies(context).values().stream()
        .collect(Collectors.toMap(getNames,
            Function.identity()))
        .keySet()
        .sort()

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
    def dependentTypeNames = provider.dependencies(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(modelType),
            Optional.empty(),
            new HashSet<>(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).values().stream()
        .collect(Collectors.toMap(getNames,
            Function.identity()))
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
