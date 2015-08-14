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
import com.google.common.collect.Ordering
import spock.lang.Specification
import springfox.documentation.schema.Model
import springfox.documentation.service.ApiDescription
import springfox.documentation.service.SecurityReference

class ApiListingBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def orderingMock = Mock(Ordering)
      def sut = new ApiListingBuilder(orderingMock)
    and:
      orderingMock.sortedCopy(value) >> value
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod       | value                   | property
      'apiVersion'        | '1.0'                   | 'apiVersion'
      'basePath'          | 'urn:base-path'         | 'basePath'
      'resourcePath'      | 'urn:resource-path'     | 'resourcePath'
      'description'       | 'test'                  | 'description'
      'position'          | 1                       | 'position'
      'produces'          | ['app/json'] as Set     | 'produces'
      'consumes'          | ['app/json'] as Set     | 'consumes'
      'protocols'         | ['https']  as Set       | 'protocols'
      'securityReferences'| [Mock(SecurityReference)]   | 'securityReferences'
      'apis'              | [Mock(ApiDescription)]  | 'apis'
      'models'            | [m1: Mock(Model)]       | 'models'
      'tags'              | ["test"] as Set         | 'tags'
  }

  def "Setting properties on the builder with null values preserves existing values"() {
    given:
      def orderingMock = Mock(Ordering)
      def sut = new ApiListingBuilder(orderingMock)
    and:
      orderingMock.sortedCopy(value) >> value
    when:
      sut."$builderMethod"(value)
    and:
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                   | property
      'apiVersion'    | '1.0'                   | 'apiVersion'
      'basePath'      | 'urn:base-path'         | 'basePath'
      'resourcePath'  | 'urn:resource-path'     | 'resourcePath'
      'description'   | 'test'                  | 'description'
      'produces'      | ['app/json'] as Set     | 'produces'
      'consumes'      | ['app/json'] as Set     | 'consumes'
      'protocols'     | ['https'] as Set        | 'protocols'
      'securityReferences'| [Mock(SecurityReference)]   | 'securityReferences'
      'apis'          | [Mock(ApiDescription)]  | 'apis'
      'models'        | [m1: Mock(Model)]       | 'models'
      'tags'          | ["test"] as Set         | 'tags'
  }
}
