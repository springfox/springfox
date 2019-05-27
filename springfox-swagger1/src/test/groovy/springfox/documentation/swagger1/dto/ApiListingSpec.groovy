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

package springfox.documentation.swagger1.dto

import org.springframework.http.MediaType

import static java.util.Collections.*

class ApiListingSpec extends InternalJsonSerializationSpec {

  ApiListing apiListing = newApiListing()

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

  def appendApiListing() {
    when:
      ApiListing apiListing = new ApiListing()
      apiListing.appendConsumes(singleton(MediaType.APPLICATION_JSON_VALUE))
      apiListing.appendProduces(singleton(MediaType.APPLICATION_JSON_VALUE))
      apiListing.appendProtocols(singleton("http"))
      apiListing.appendAuthorizations([Mock(Authorization)])
      apiListing.appendApis([Mock(ApiDescription)])
      apiListing.appendModels(
          ['someModel':
               new ModelDto('id', 'name', 'qtype',
                   ['aprop': new ModelPropertyDto("aProp", 'ptype', 'qtype', 0, false, 'pdesc', null)]
                   , 'desc', null, null, null)
          ])
    then:
      apiListing.consumes.size() > 0
      apiListing.produces.size() > 0
      apiListing.apis.size() > 0
      apiListing.protocols.size() > 0
      apiListing.models.size() > 0
      apiListing.authorizations.size() > 0
  }

  def appendEmptyApiListingValues() {
    when:
      ApiListing apiListing = new ApiListing()
      apiListing.appendConsumes(new HashSet<>())
      apiListing.appendProduces(new HashSet<>())
      apiListing.appendProtocols(new HashSet<>())
      apiListing.appendAuthorizations([])
      apiListing.appendApis([])
      apiListing.appendModels(new HashMap<>())
    then:
      apiListing.consumes == null
      apiListing.produces == null
      apiListing.apis == null
      apiListing.protocols == null
      apiListing.models == null
      apiListing.authorizations == null
  }

  def appendNullApiListingValues() {
    when:
      ApiListing apiListing = new ApiListing()
      apiListing.appendConsumes(null)
      apiListing.appendProduces(null)
      apiListing.appendProtocols(null)
      apiListing.appendAuthorizations(null)
      apiListing.appendApis(null)
      apiListing.appendModels(null)
    then:
      apiListing.consumes == null
      apiListing.produces == null
      apiListing.apis == null
      apiListing.protocols == null
      apiListing.models == null
      apiListing.authorizations == null
  }


  def "should serialize"() {
    expect:
      writePretty(apiListing) == '''{
  "apiVersion" : "1",
  "apis" : [ ],
  "basePath" : "/base",
  "consumes" : [ "application/json" ],
  "models" : {
    "someModel" : {
      "description" : "desc",
      "id" : "name",
      "properties" : {
        "aprop" : {
          "description" : "pdesc",
          "required" : false,
          "type" : "ptype"
        }
      }
    }
  },
  "produces" : [ "application/json" ],
  "resourcePath" : "/resource",
  "swaggerVersion" : "1.2"
}'''

  }

  def "should pass coverage"() {
    expect:
      apiListing.with {
        getApis()
        getApiVersion()
        getAuthorizations()
        getBasePath()
        getConsumes()
        getProduces()
        getDescription()
        getModels()
        getPosition()
        getProtocols()
        getResourcePath()
        getSwaggerVersion()
      }
  }
}
