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
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.spi.schema.contexts.ModelContext

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class TypeNameExtractorSpec extends SchemaSpecification {
  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  def uniqueTypeNameAdjuster = new TypeNameIndexingAdapter();
  def TypeResolver resolver = new TypeResolver()
  def "Response class for container types are inferred correctly"() {
    given:
      def context = returnValue("group",
          containerType,
          Optional.absent(),
          SWAGGER_12,
          uniqueTypeNameAdjuster,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
    expect:
      typeNameExtractor.typeName(context) == name
      typeNameExtractor.typeName(withAdjustedTypeName(context)) == name

    where:
      containerType                  | name
      genericListOfSimpleType()      | "List"
      genericListOfInteger()         | "List"
      erasedList()                   | "List"
      genericSetOfSimpleType()       | "Set"
      genericSetOfInteger()          | "Set"
      erasedSet()                    | "Set"
      genericClassWithGenericField() | "GenericType«ResponseEntityAlternative«SimpleType»»"
      hashMap(String, SimpleType)    | "Map«string,SimpleType»"
      hashMap(String, String)        | "Map«string,string»"
  }

  def "Input class for container types are inferred correctly"() {
    given:
      def context = inputParam("group",
          containerType,
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          uniqueTypeNameAdjuster,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
    expect:
      typeNameExtractor.typeName(context) == name
      typeNameExtractor.typeName(withAdjustedTypeName(context)) == name

    where:
      containerType                  | name
      genericListOfSimpleType()      | "List"
      genericListOfInteger()         | "List"
      erasedList()                   | "List"
      genericSetOfSimpleType()       | "Set"
      genericSetOfInteger()          | "Set"
      erasedSet()                    | "Set"
      genericClassWithGenericField() | "GenericType«ResponseEntityAlternative«SimpleType»»"
      hashMap(String, SimpleType)    | "Map«string,SimpleType»"
      hashMap(String, String)        | "Map«string,string»"
  }

  def "Extractor for different contexts with same type produced different names"() {
    given:
      def contextInput = inputParam("group",
          hashMap(String, SimpleType),
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          uniqueTypeNameAdjuster,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      def contextReturn = returnValue("group",
        genericClassWithGenericField(),
        Optional.absent(),
        SWAGGER_12,
        uniqueTypeNameAdjuster,
        alternateTypeProvider(),
        namingStrategy,
        ImmutableSet.builder().build())
    expect:
      def nameInput = typeNameExtractor.typeName(contextInput)
      nameInput == "Map«string,SimpleType»"

      def nameReturn = typeNameExtractor.typeName(contextReturn)
      nameReturn == "GenericType«ResponseEntityAlternative«SimpleType»»"
    and:      
      def nameInputAdjusted = typeNameExtractor.typeName(withAdjustedTypeName(contextInput))
      nameInputAdjusted == "Map«string,SimpleType_2»"

      def nameReturnAdjusted = typeNameExtractor.typeName(withAdjustedTypeName(contextReturn))
      nameReturnAdjusted == "GenericType«ResponseEntityAlternative«SimpleType_1»»"
  }

  def "Extractor for different contexts with same type produced same names if equality is present"() {
    given:
      def contextInput = inputParam("group",
          hashMap(String, SimpleType),
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          uniqueTypeNameAdjuster,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      def contextInputWithView = inputParam("group",
        hashMap(String, SimpleType),
        Optional.of(resolver.resolve(Views.FirstView.class)),
        new HashSet<>(),
        SWAGGER_12,
        uniqueTypeNameAdjuster,
        alternateTypeProvider(),
        namingStrategy,
        ImmutableSet.builder().build())
      def contextReturn = returnValue("group",
        hashMap(String, SimpleType),
        Optional.absent(),
        SWAGGER_12,
        uniqueTypeNameAdjuster,
        alternateTypeProvider(),
        namingStrategy,
        ImmutableSet.builder().build())
    expect:
      def nameInput = typeNameExtractor.typeName(contextInput)
      nameInput == "Map«string,SimpleType»"

      def nameReturn = typeNameExtractor.typeName(contextReturn)
      nameReturn == "Map«string,SimpleType»"

      fromParent(contextInput, resolver.resolve(SimpleType))
          .assumeEqualsTo(fromParent(contextReturn, resolver.resolve(SimpleType)))
      fromParent(contextInput, resolver.resolve(SimpleType))
          .assumeEqualsTo(fromParent(contextInputWithView, resolver.resolve(SimpleType)))

      def nameInputWithView = typeNameExtractor.typeName(contextInputWithView)
      nameInput == "Map«string,SimpleType»"

      fromParent(contextInput, resolver.resolve(SimpleType))
          .assumeEqualsTo(fromParent(contextInputWithView, resolver.resolve(SimpleType)))
      fromParent(contextReturn, resolver.resolve(SimpleType))
          .assumeEqualsTo(fromParent(contextInputWithView, resolver.resolve(SimpleType)))
    and:
      def nameInputAdjusted = typeNameExtractor.typeName(ModelContext.withAdjustedTypeName(contextInput))
      nameInputAdjusted == "Map«string,SimpleType»"

      def nameInputWithViewAdjusted = typeNameExtractor.typeName(ModelContext.withAdjustedTypeName(contextInputWithView))
      nameInputAdjusted == "Map«string,SimpleType»"

      def nameReturnAdjusted = typeNameExtractor.typeName(ModelContext.withAdjustedTypeName(contextReturn))
      nameReturnAdjusted == "Map«string,SimpleType»"
  }
  //TODO: test cases for parent (withAndWithout)
}
