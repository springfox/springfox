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

package springfox.documentation.builders

import spock.lang.Specification
import springfox.documentation.service.AuthorizationScope

class AuthorizationBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new AuthorizationBuilder()
      AuthorizationScope [] authScopes = new AuthorizationScope[1]
      authScopes[0] = Mock(AuthorizationScope)
    and:
      sut.type('oAuth')
      sut.scopes(authScopes)
    when:
      def built = sut.build()
    then:
      built.type == 'oAuth'
      built.scopes.size() == 1
  }

  def "Throws NPE when the scopes are not set"() {
    given:
      def sut = new AuthorizationBuilder()
    and:
      sut.type(null)
    when:
      sut.build()
    then:
      thrown(NullPointerException)
  }

  def "Preserves initialized type when setting null values"() {
    given:
      def sut = new AuthorizationBuilder()
      AuthorizationScope [] authScopes = new AuthorizationScope[1]
      authScopes[0] = Mock(AuthorizationScope)
      sut.scopes(authScopes)
    when:
      sut.type('oAuth')
      sut.type(null)
    and:
      def built = sut.build()
    then:
      built.type == 'oAuth'
      built.scopes.size() == 1
  }

}
