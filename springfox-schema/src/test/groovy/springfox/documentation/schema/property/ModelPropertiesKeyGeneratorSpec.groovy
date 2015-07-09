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
package springfox.documentation.schema.property

import spock.lang.Specification
import springfox.documentation.schema.ModelCacheKeys
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.spi.schema.contexts.ModelContext

@Mixin(TypesForTestingSupport)
class ModelPropertiesKeyGeneratorSpec extends Specification {

  def "Illegal argument or NPE when param isnt of type ResolvedType and ModelContext" () {
    given:
      ModelPropertiesKeyGenerator sut = new ModelPropertiesKeyGenerator()
    when:
      sut.generate(null, null, args)
    then:
      thrown(Exception)
    where:
      args << [[], [null], ["hello"], null, returnType(simpleType()), [simpleType()]]
  }

  def "Exception when param isnt of type ModelContext" () {
    when:
      ModelCacheKeys.modelPropertiesKey(args)
    then:
      thrown(Exception)
    where:
      args << [[], [null], [""], null]
  }

  def "Generates key when param is of type ResolvedType and ModelContext" () {
    given:
      ModelPropertiesKeyGenerator sut = new ModelPropertiesKeyGenerator()
    and:

    when:
      def key = sut.generate(null, null, context)
    then:
      key == expectedKey
    where:
      context                                   | expectedKey
      returnType(genericListOfSimpleType())     | "java.util.List<springfox.documentation.schema.SimpleType>(true)"
      input(genericListOfSimpleType())          | "java.util.List<springfox.documentation.schema.SimpleType>(false)"
      genericListOfSimpleType()                 | "java.util.List<springfox.documentation.schema.SimpleType>"
  }

  def Object[] returnType(type) {
    def modelContext = Mock(ModelContext)
    modelContext.isReturnType() >> true
    [type, modelContext]
  }

  def Object[] input(type) {
    def modelContext = Mock(ModelContext)
    modelContext.isReturnType() >> false
    [type, modelContext]
  }
}
