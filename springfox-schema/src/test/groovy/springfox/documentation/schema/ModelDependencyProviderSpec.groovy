/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import spock.lang.Shared
import spock.lang.Unroll

import static java.util.Collections.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ModelDependencyProviderSpec extends SchemaSpecification {
  @Shared def namingStrategy = new DefaultGenericTypeNamingStrategy()
  @Shared def resolver = new TypeResolver()

  @Unroll
  def "dependencies are inferred correctly"() {
    given:
    def context = inputParam(
        "0_0",
        "group",
        resolver.resolve(modelType),
        Optional.empty(),
        new HashSet<>(),
        documentationType,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    def dependentTypes = modelDependencyProvider.dependentModels(context)
    def dependentTypeNames = dependentTypes.collect() {
      typeNameExtractor.typeName(
          inputParam(
              "0_0",
              "group",
              it,
              Optional.empty(),
              new HashSet<>(),
              documentationType,
              alternateTypeProvider(),
              namingStrategy,
              emptySet()))
    }.unique()
        .sort()

    expect:
    dependencies == dependentTypeNames

    where:
    modelType                       | dependencies
    simpleType()                    | []
    complexType()                   | ["Category"]
    enumType()                      | []
    typeWithLists()                 | ["List", "Category", "ComplexType", "Substituted"].sort()
    typeWithSets()                  | ["Set", "Category", "ComplexType"].sort()
    typeWithArrays()                | ["Array", "Category", "ComplexType", "List", "Substituted"].sort()
    genericClass()                  | ["List", "SimpleType"].sort()
    genericClassWithListField()     | ["List", "SimpleType"].sort()
    genericClassWithGenericField()  | ["List", "ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()
    genericClassWithDeepGenerics()  | ["List", "ResponseEntityAlternative«List«SimpleType»»", "SimpleType"].sort()
    genericCollectionWithEnum()     | ["List"].sort()
    recursiveType()                 | ["SimpleType"]
    listOfMapOfStringToString()     | ["Map«string,string»"]
    listOfMapOfStringToSimpleType() | ["Map«string,SimpleType»", "SimpleType"]
    listOfErasedMap()               | []
    nestedMaps()                    | ["ApplicationLang", "Language", "LanguageResponse", "LanguageText", "List"]
  }

  @Unroll
  def "dependencies are inferred correctly for return parameters"() {
    given:
    def context = returnValue(
        "0_0",
        "group",
        resolver.resolve(modelType),
        Optional.empty(),
        documentationType,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    def dependentTypes = modelDependencyProvider.dependentModels(context)
    def dependentTypeNames = dependentTypes.collect() {
      typeNameExtractor.typeName(
          returnValue(
              "0_0",
              "group",
              it,
              Optional.empty(),
              documentationType,
              alternateTypeProvider(),
              namingStrategy,
              emptySet()))
    }.unique()
        .sort()
    expect:
    dependencies == dependentTypeNames

    where:
    modelType                       | dependencies
    simpleType()                    | []
    complexType()                   | ["Category"]
    enumType()                      | []
    inheritedComplexType()          | ["Category"]
    typeWithLists()                 | ["List", "Category", "ComplexType", "Substituted"].sort()
    typeWithSets()                  | ["Set", "Category", "ComplexType"].sort()
    typeWithArrays()                | ["Array", "Category", "ComplexType", "List", "Substituted"].sort()
    genericClass()                  | ["List", "SimpleType"].sort()
    genericClassWithListField()     | ["List", "SimpleType"].sort()
    genericClassWithGenericField()  | ["List", "ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()
    genericClassWithDeepGenerics()  | ["List", "ResponseEntityAlternative«List«SimpleType»»", "SimpleType"].sort()
    genericCollectionWithEnum()     | ["List"].sort()
    recursiveType()                 | ["SimpleType"]
    listOfMapOfStringToString()     | ["Map«string,string»"]
    listOfMapOfStringToSimpleType() | ["Map«string,SimpleType»", "SimpleType"]
    listOfErasedMap()               | []
    nestedMaps()                    | ["ApplicationLang", "Language", "LanguageResponse", "LanguageText", "List"]
  }

}
