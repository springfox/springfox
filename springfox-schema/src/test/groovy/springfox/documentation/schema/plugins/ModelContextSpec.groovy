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
package springfox.documentation.schema.plugins

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.google.common.base.Optional
import com.google.common.collect.ImmutableSet

import groovy.lang.Mixin
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.AlternateTypesSupport
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ExampleEnum
import springfox.documentation.schema.ExampleWithEnums
import springfox.documentation.schema.TypeNameIndexingAdapter
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.schema.contexts.ModelContext

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

import java.util.Set

@Mixin(TypesForTestingSupport)
class ModelContextSpec extends Specification {
  @Shared
  AlternateTypeProvider provider = Mock(AlternateTypeProvider)
  @Shared
  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  @Shared
  def typeNameAdjuster = new TypeNameIndexingAdapter()

  def "ModelContext equals works as expected"() {
    given:
      ModelContext context = inputParam(
          "group",
          resolver.resolve(ExampleEnum),
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          typeNameAdjuster,
          provider,
          namingStrategy,
          ImmutableSet.builder().build())
    expect:
      context.equals(test) == expectedEquality
      context.equals(context)
    where:
      test                                               | expectedEquality
      inputParam(resolver.resolve(ExampleEnum))      | true
      inputParam(resolver.resolve(ExampleWithEnums)) | false
      returnValue(resolver.resolve(ExampleEnum))     | false
      ExampleEnum                                        | false
  }

  def inputParam(ResolvedType ofType) {
    inputParam("group",
        ofType,
        Optional.absent(),
        new HashSet<>(),
        SWAGGER_12,
        typeNameAdjuster,
        provider,
        namingStrategy,
        ImmutableSet.builder().build())
  }

  def returnValue(ResolvedType ofType) {
    returnValue("group",
        ofType,
        Optional.absent(),
        SWAGGER_12,
        typeNameAdjuster,
        provider,
        namingStrategy,
        ImmutableSet.builder().build())
  }

  def "ModelContext hashcode generated takes into account immutable values"() {
    given:
      ModelContext context = inputParam("group",
          resolver.resolve(ExampleEnum),
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          typeNameAdjuster,
          provider,
          namingStrategy,
          ImmutableSet.builder().build())
      ModelContext other = inputParam("group",
          resolver.resolve(ExampleEnum),
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          typeNameAdjuster,
          provider,
          namingStrategy,
          ImmutableSet.builder().build())
      ModelContext otherReturn = returnValue("group",
          resolver.resolve(ExampleEnum),
          Optional.absent(),
          SWAGGER_12,
          typeNameAdjuster,
          provider,
          namingStrategy,
          ImmutableSet.builder().build())
    expect:
      context.hashCode() == other.hashCode()
      context.hashCode() != otherReturn.hashCode()
  }
}
