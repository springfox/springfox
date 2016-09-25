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

package springfox.documentation

import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Ignore
import spock.lang.Specification
import springfox.documentation.builders.MockRequestHandler

@Ignore("Because this needs to be moved to implementations")
class RequestHandlerSpec extends Specification {
  def "tests getters and setters" (){
    given:
      def reqMapping = new RequestMappingInfo(null,null,null,null,null,null, null)
      RequestHandler sut = new MockRequestHandler(reqMapping, Mock(HandlerMethod))
    expect:
      sut.with {
        getPatternsCondition()
        getHandlerMethod()
        getRequestMapping()
      }
  }
}
