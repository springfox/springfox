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
import springfox.documentation.service.Contact
import springfox.documentation.service.ListVendorExtension
import springfox.documentation.service.VendorExtension

class ApiInfoBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ApiInfoBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    and:
      sut.contact(new Contact("Some contact", "urn:test", "some@contact.com"))
      def buildWithContactString = sut.build()
    then:
      built."$property" == value
      buildWithContactString.contact.name == "Some contact"
      buildWithContactString.contact.email == "some@contact.com"
      buildWithContactString.contact.url == "urn:test"

    where:
      builderMethod       | value                      | property
      'version'           | '1.0'                      | 'version'
      'title'             | 'title'                    | 'title'
      'termsOfServiceUrl' | 'urn:tos'                  | 'termsOfServiceUrl'
      'description'       | 'test'                     | 'description'
      'contact'           | new Contact("a", "b", "c") | 'contact'
      'license'           | 'license'                  | 'license'
      'licenseUrl'        | 'urn:license'              | 'licenseUrl'
      'extensions'        | extensions()                  | 'vendorExtensions'
  }

  List<VendorExtension> extensions() {
    return Arrays.asList(new ListVendorExtension<String>("test", Arrays.asList("Test")))
  }

}
