/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.spring.web.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.stereotype.Component
import spock.lang.Specification
import spock.lang.Unroll

@Component
class JacksonSerializerConventionSpec extends Specification {
  def resolver = new TypeResolver()

  @Unroll
  def "Identifies serializers and deserializers for #original.simpleName" () {
    given:
      def sut = conventionInThisPackage()
    when:
      def rules = sut.rules()
    then:
      rules.find { it.original == resolver.resolve(original) }?.alternate == substitute ?: resolver.resolve(substitute)

    where:
      original            | substitute
      Same                | A
      Different           | A
      MissingSerialize    | B
      MissingDeserialize  | A
      MissingBoth         | null
  }

  def conventionInThisPackage() {
    new JacksonSerializerConvention(resolver, "springfox.documentation.spring.web.plugins")
  }

  @JsonSerialize(as=A)
  @JsonDeserialize(as=A)
  class Same {
  }

  @JsonSerialize(as=A)
  @JsonDeserialize(as=B)
  class Different {
  }

  @JsonDeserialize(as=B)
  class MissingSerialize {
  }

  @JsonSerialize(as=A)
  class MissingDeserialize {
  }

  class MissingBoth {
  }

  class A {
  }

  class B {
  }
}
