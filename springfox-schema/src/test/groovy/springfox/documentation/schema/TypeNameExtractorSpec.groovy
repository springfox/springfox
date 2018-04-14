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

import com.google.common.collect.ImmutableSet
import springfox.documentation.schema.mixins.TypesForTestingSupport

import java.lang.reflect.Method
import java.lang.reflect.Modifier

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class TypeNameExtractorSpec extends SchemaSpecification {
  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  def "Response class for container types are inferred correctly"() {
    given:
      def context = returnValue("group",
          containerType,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
    expect:
      typeNameExtractor.typeName(context) == name

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
      def context = returnValue("group",
          containerType,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
    expect:
      typeNameExtractor.typeName(context) == name

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

  def "TypeNameExtractor.asResolved(Model) should be accessible from subclasses" (){
    given:
      Method mapPropertiesMethod = TypeNameExtractor.class.getDeclaredMethod("asResolved", java.lang.reflect.Type.class);
    when:
      def modifiers = mapPropertiesMethod.getModifiers();
    then:
      Modifier.isProtected(modifiers);
  }
  //TODO: test cases for parent (withAndWithout)
}
