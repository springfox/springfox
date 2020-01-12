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
import spock.lang.Specification
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider

import static java.util.Collections.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class CachingModelProviderSpec extends Specification implements TypesForTestingSupport {
  @Shared def resolver = new TypeResolver()
   
  def "Implementation caches the invocations" () {
    given:
      def context = inputParam("0_0",
          "group",
          resolver.resolve(complexType()),
          Optional.empty(),
          new HashSet<>(),
          DocumentationType.SWAGGER_2,
          new AlternateTypeProvider([]),
          new CodeGenGenericTypeNamingStrategy(),
          emptySet())
      def model = aModel()
      def mock = Mock(ModelProvider) {
        modelFor(context) >> Optional.of(model)
      }
    when:
      def sut = new CachingModelProvider(mock)
    then:
      sut.modelFor(context) == sut.modelFor(context)
  }

  def "Cache misses do not not result in errors" () {
    given:
      def context = inputParam("0_0",
          "group",
          resolver.resolve(complexType()),
          Optional.empty(),
          new HashSet<>(),
          DocumentationType.SWAGGER_2,
          new AlternateTypeProvider([]),
          new CodeGenGenericTypeNamingStrategy(),
          emptySet())
      def mock = Mock(ModelProvider) {
        modelFor(context) >> { throw new NullPointerException() }
      }
    when:
      def sut = new CachingModelProvider(mock)
    then:
      !sut.modelFor(context).isPresent()
  }

  def aModel() {
    Mock(Model)
  }
}
