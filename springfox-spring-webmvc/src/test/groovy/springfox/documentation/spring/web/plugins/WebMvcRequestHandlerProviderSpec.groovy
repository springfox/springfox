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
import org.springframework.mock.web.MockServletContext
import spock.lang.Specification
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver

class WebMvcRequestHandlerProviderSpec extends Specification {
  def "when handler mappings is empty or null" () {
    given:
      WebMvcRequestHandlerProvider sut = new WebMvcRequestHandlerProvider(
          Optional.of(new MockServletContext("/")),
          new HandlerMethodResolver(new TypeResolver()),
          handlers)
    expect:
      sut.requestHandlers().size() == 0
    where:
      handlers << [null, []]
  }
}
