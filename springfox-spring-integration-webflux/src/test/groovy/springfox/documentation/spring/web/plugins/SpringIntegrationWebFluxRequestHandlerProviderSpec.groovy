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

package springfox.documentation.spring.web.plugins

import org.springframework.integration.webflux.inbound.WebFluxIntegrationRequestMappingHandlerMapping
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.result.method.RequestMappingInfo
import spock.lang.Specification
import springfox.documentation.spring.web.SpringIntegrationWebFluxRequestHandler
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver

class SpringIntegrationWebFluxRequestHandlerProviderSpec extends Specification {
  def methodResolver = Mock(HandlerMethodResolver)
  def handlerMapping = Mock(WebFluxIntegrationRequestMappingHandlerMapping)
  def handlerMappings = [handlerMapping]
  SpringIntegrationParametersProvider parametersProvider = Mock(SpringIntegrationParametersProvider)

  def provider = new SpringIntegrationWebFluxRequestHandlerProvider(methodResolver,
      handlerMappings, parametersProvider)

  def "Provides request handlers"() {
    given:
    def requestMappingInfo = RequestMappingInfo.paths("/foo").build()
    def handlerMethod = Mock(HandlerMethod)
    def handlerMethods = [(requestMappingInfo): handlerMethod]
    handlerMapping.getHandlerMethods() >> handlerMethods

    when:
    def handlers = provider.requestHandlers()

    then:
    handlers.size() == 1
    handlers[0] instanceof SpringIntegrationWebFluxRequestHandler
  }
}
