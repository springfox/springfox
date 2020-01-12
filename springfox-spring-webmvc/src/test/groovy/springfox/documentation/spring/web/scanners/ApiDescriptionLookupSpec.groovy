/*
 *
 *  Copyright 2015-2016 the original author or authors.
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
package springfox.documentation.spring.web.scanners

import spock.lang.Specification
import springfox.documentation.RequestHandler
import springfox.documentation.RequestHandlerKey
import springfox.documentation.service.ApiDescription
import springfox.documentation.spring.web.mixins.RequestMappingSupport

class ApiDescriptionLookupSpec extends Specification implements RequestMappingSupport {
  def "Given a controller method be able to lookup the api description" () {
    given:
      ApiDescriptionLookup sut = new ApiDescriptionLookup()
    and:
      def apiDescription = Mock(ApiDescription)
      def requestHandler = Mock(RequestHandler)
      def requestHandlerKey = new RequestHandlerKey(
          [] as Set,
          [] as Set,
          [] as Set,
          [] as Set)
    and:
      requestHandler.key() >> requestHandlerKey
    when:
      sut.add(requestHandlerKey, apiDescription)
    then:
      sut.description(requestHandler) == apiDescription
    and:
      sut.description(Mock(RequestHandler)) == null
  }
}
