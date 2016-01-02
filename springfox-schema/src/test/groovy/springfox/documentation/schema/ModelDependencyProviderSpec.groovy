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

package springfox.documentation.schema

import spock.lang.Unroll
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class ModelDependencyProviderSpec extends SchemaSpecification {
  def namingStrategy = new DefaultGenericTypeNamingStrategy()

  @Unroll
  def "dependencies are inferred correctly" () {
    given:
      def context = inputParam(modelType, documentationType, alternateTypeProvider(), namingStrategy)
      def dependentTypes = modelDependencyProvider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect() {
          typeNameExtractor.typeName(inputParam(it, documentationType, alternateTypeProvider(), namingStrategy))
        }.unique()
        .sort()

    expect:
     dependencies == dependentTypeNames

    where:
      modelType                       | dependencies
      simpleType()                    | []
      complexType()                   | ["Category"]
      enumType()                      | []
      typeWithLists()                 | ["List", "Category",  "ComplexType", "Substituted"].sort()
      typeWithSets()                  | ["Set", "Category",  "ComplexType"].sort()
      typeWithArrays()                | ["Array", "Category", "ComplexType", "List", "Substituted"].sort()
      genericClass()                  | ["List", "SimpleType"].sort()
      genericClassWithListField()     | ["List", "SimpleType"].sort()
      genericClassWithGenericField()  | ["HttpHeaders", "List", "ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()
      genericClassWithDeepGenerics()  | ["HttpHeaders", "List", "ResponseEntityAlternative«List«SimpleType»»",
                                         "SimpleType"].sort()
      genericCollectionWithEnum()     | ["Collection«string»", "List"].sort()
      recursiveType()                 | ["SimpleType"]
      listOfMapOfStringToString()     | ["Map«string,string»"]
      listOfMapOfStringToSimpleType() | ["Map«string,SimpleType»", "SimpleType"]
      listOfErasedMap()               | []

  }

  @Unroll
  def "dependencies are inferred correctly for return parameters" () {
    given:
      def context = returnValue(modelType, documentationType, alternateTypeProvider(), namingStrategy)
      def dependentTypes = modelDependencyProvider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect() {
            typeNameExtractor.typeName(returnValue(it, documentationType, alternateTypeProvider(), namingStrategy))
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
      typeWithLists()                 | ["List", "Category",  "ComplexType", "Substituted"].sort()
      typeWithSets()                  | ["Set", "Category",  "ComplexType"].sort()
      typeWithArrays()                | ["Array", "Category", "ComplexType", "List", "Substituted"].sort()
      genericClass()                  | ["List", "SimpleType"].sort()
      genericClassWithListField()     | ["List", "SimpleType"].sort()
      genericClassWithGenericField()  | ["HttpHeaders", "List", "ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()
      genericClassWithDeepGenerics()  | ["HttpHeaders", "List", "ResponseEntityAlternative«List«SimpleType»»",
                                         "SimpleType"].sort()
      genericCollectionWithEnum()     | ["Collection«string»", "List"].sort()
      recursiveType()                 | ["SimpleType"]
      listOfMapOfStringToString()     | ["Map«string,string»"]
      listOfMapOfStringToSimpleType() | ["Map«string,SimpleType»", "SimpleType"]
      listOfErasedMap()               | []
  }



}
