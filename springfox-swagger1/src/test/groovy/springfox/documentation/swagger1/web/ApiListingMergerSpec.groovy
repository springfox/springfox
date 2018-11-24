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
package springfox.documentation.swagger1.web

import org.springframework.http.MediaType
import spock.lang.Specification
import springfox.documentation.swagger1.dto.ApiListing
import springfox.documentation.swagger1.dto.ModelDto
import springfox.documentation.swagger1.dto.ModelPropertyDto

import static java.util.Collections.*

class ApiListingMergerSpec extends Specification {

  def "it returns api listing absent when collection is empty"(){
    when:
      def merged = ApiListingMerger.mergedApiListing(apiListings)
    then:
      !merged.isPresent()
    where:
      apiListings << [new ArrayList<>(), null]
  }

  def "it returns api listing absent when collection has one element"(){
    when:
      def merged = ApiListingMerger.mergedApiListing(singletonList(Mock(ApiListing)))
    then:
      merged.isPresent()
  }

  def "it returns api listing absent when collection has more than one element"(){
    when:
      def merged = ApiListingMerger.mergedApiListing([newApiListing(), newApiListing()])
    then:
      merged.isPresent()
  }

  def newApiListing() {
    ApiListing apiListing = new ApiListing()
    apiListing.apiVersion = '1'
    apiListing.swaggerVersion = '1.2'
    apiListing.basePath = '/base'
    apiListing.resourcePath= '/resource'
    apiListing.consumes= [MediaType.APPLICATION_JSON_VALUE]
    apiListing.produces= [MediaType.APPLICATION_JSON_VALUE]
    apiListing.protocols= []
    apiListing.authorizations= []
    apiListing.apis = []
    apiListing.models =
        ['someModel':
             new ModelDto('id', 'name', 'qtype',
                 ['aprop': new ModelPropertyDto("aProp", 'ptype', 'qtype', 0, false, 'pdesc', null)]
                 , 'desc', null, null, null)
        ]
    apiListing.description = 'description'
    apiListing.position = 0
    apiListing
  }
}
