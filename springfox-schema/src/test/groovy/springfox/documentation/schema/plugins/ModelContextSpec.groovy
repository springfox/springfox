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
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ExampleEnum
import springfox.documentation.schema.ExampleWithEnums
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.contexts.ModelContext

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ModelContextSpec extends Specification implements TypesForTestingSupport {
  @Shared
  AlternateTypeProvider provider = Mock(AlternateTypeProvider)
  @Shared
  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  @Shared def resolver = new TypeResolver()

  def "ModelContext equals works as expected"() {
    given:
    ModelContext context = inputParam(
        "0_0",
        "group",
        resolver.resolve(ExampleEnum),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        provider,
        namingStrategy,
        emptySet())

    expect:
    (context == test) == expectedEquality
    context == context

    where:
    test                                           | expectedEquality
    inputParam(resolver.resolve(ExampleEnum))      | true
    inputParam(resolver.resolve(ExampleWithEnums)) | false
    returnValue(resolver.resolve(ExampleEnum))     | false
    ExampleEnum                                    | false
  }

  def inputParam(ResolvedType ofType) {
    inputParam("0_0",
        "group",
        ofType,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        provider,
        namingStrategy,
        emptySet())
  }

  def returnValue(ResolvedType ofType) {
    returnValue("0_0",
        "group",
        ofType,
        Optional.empty(),
        SWAGGER_12,
        provider,
        namingStrategy,
        emptySet())
  }

  def "ModelContext hashcode generated takes into account immutable values"() {
    given:
    ModelContext context = inputParam("0_0",
        "group",
        resolver.resolve(ExampleEnum),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        provider,
        namingStrategy,
        emptySet())

    ModelContext other = inputParam("0_0",
        "group",
        resolver.resolve(ExampleEnum),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        provider,
        namingStrategy,
        emptySet())

    ModelContext otherReturn = returnValue("0_0",
        "group",
        resolver.resolve(ExampleEnum),
        Optional.empty(),
        SWAGGER_12,
        provider,
        namingStrategy,
        emptySet())

    expect:
    context.hashCode() == other.hashCode()
    context.hashCode() != otherReturn.hashCode()
  }
}
