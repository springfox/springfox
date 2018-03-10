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
package springfox.documentation.schema.property

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.google.common.collect.ImmutableSet
import spock.lang.Specification
import springfox.documentation.schema.CodeGenGenericTypeNamingStrategy
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider

import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin(TypesForTestingSupport)
class CachingModelPropertiesProviderSpec extends Specification {
  def "Implementation caches the invocations"() {
    given:
    def context = inputParam("group",
        complexType(),
        DocumentationType.SWAGGER_2,
        new AlternateTypeProvider([]),
        new CodeGenGenericTypeNamingStrategy(),
        ImmutableSet.builder().build())
    def property = aProperty()
    def mock = Mock(ModelPropertiesProvider) {
      propertiesFor(_, context) >> [property]
    }

    when:
    def sut = new CachingModelPropertiesProvider(new TypeResolver(), mock)

    then:
    sut.propertiesFor(Mock(ResolvedType), context) == sut.propertiesFor(Mock(ResolvedType), context)
  }

  def "When cache miss occurs"() {
    given:
    def context = inputParam("group",
        complexType(),
        DocumentationType.SWAGGER_2,
        new AlternateTypeProvider([]),
        new CodeGenGenericTypeNamingStrategy(),
        ImmutableSet.builder().build())
    def mock = Mock(ModelPropertiesProvider) {
      propertiesFor(_, context) >> { throw new NullPointerException("") }
    }

    when:
    def sut = new CachingModelPropertiesProvider(new TypeResolver(), mock)

    then:
    0 == sut.propertiesFor(Mock(ResolvedType), context).size()
  }

  def aProperty() {
    Mock(ModelProperty)
  }
}
