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

import org.springframework.integration.http.inbound.BaseHttpInboundEndpoint
import org.springframework.integration.http.inbound.IntegrationRequestMappingHandlerMapping
import org.springframework.util.ReflectionUtils
import org.springframework.web.HttpRequestHandler
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import springfox.documentation.spring.web.SpringIntegrationWebMvcRequestHandler
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SpringIntegrationWebMvcRequestHandlerProviderSpec extends Specification {
  def methodResolver = Mock(HandlerMethodResolver)
  def handler = Mock(BaseHttpInboundEndpoint)
  def method = ReflectionUtils.findMethod(
      HttpRequestHandler.class,
      "handleRequest",
      HttpServletRequest.class,
      HttpServletResponse.class)

  def parametersProvider = Mock(SpringIntegrationParametersProvider)

  def "Provides request handlers"() {
    given:
    def handlerMapping = new IntegrationRequestMappingHandlerMapping()
    def requestMappingInfo = RequestMappingInfo.paths("/foo").build()
    handlerMapping.registerMapping(requestMappingInfo, handler, method)
    def handlerMappings = [handlerMapping]

    def provider = new SpringIntegrationWebMvcRequestHandlerProvider(
        Optional.empty(), methodResolver, handlerMappings, parametersProvider)

    when:
    def handlers = provider.requestHandlers()

    then:
    handlers.size() == 1
    handlers[0] instanceof SpringIntegrationWebMvcRequestHandler
  }
}