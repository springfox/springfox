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

import com.google.common.collect.ImmutableSet
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ExampleEnum
import springfox.documentation.schema.ExampleWithEnums
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.contexts.ModelContext

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ModelContextSpec extends Specification {
  @Shared
  AlternateTypeProvider provider = Mock(AlternateTypeProvider)
  @Shared
  def namingStrategy = new DefaultGenericTypeNamingStrategy()

  def "ModelContext equals works as expected"() {
    given:
      ModelContext context = inputParam(
          "group",
          ExampleEnum,
          SWAGGER_12,
          provider,
          namingStrategy,
          ImmutableSet.builder().build())
    expect:
      context.equals(test) == expectedEquality
      context.equals(context)
    where:
      test                         | expectedEquality
      inputParam(ExampleEnum)      | true
      inputParam(ExampleWithEnums) | false
      returnValue(ExampleEnum)     | false
      ExampleEnum                  | false
  }

  def inputParam(Class ofType) {
    inputParam("group",
        ofType,
        SWAGGER_12,
        provider,
        namingStrategy,
        ImmutableSet.builder().build())
  }

  def returnValue(Class ofType) {
    returnValue("group",
        ofType,
        SWAGGER_12,
        provider,
        namingStrategy,
        ImmutableSet.builder().build())
  }

  def "ModelContext hashcode generated takes into account immutable values"() {
    given:
      ModelContext context = inputParam("group",
          ExampleEnum,
          SWAGGER_12,
          provider,
          namingStrategy,
          ImmutableSet.builder().build())
      ModelContext other = inputParam("group",
          ExampleEnum,
          SWAGGER_12,
          provider,
          namingStrategy,
          ImmutableSet.builder().build())
      ModelContext otherReturn = returnValue("group",
          ExampleEnum,
          SWAGGER_12,
          provider,
          namingStrategy,
          ImmutableSet.builder().build())
    expect:
      context.hashCode() == other.hashCode()
      context.hashCode() != otherReturn.hashCode()
  }
}
