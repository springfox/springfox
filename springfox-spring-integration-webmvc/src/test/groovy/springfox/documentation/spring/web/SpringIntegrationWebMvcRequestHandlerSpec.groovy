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

package springfox.documentation.spring.web

import com.fasterxml.classmate.ResolvedType
import io.swagger.annotations.Api
import org.springframework.integration.http.inbound.BaseHttpInboundEndpoint
import org.springframework.integration.webflux.inbound.WebFluxInboundEndpoint
import org.springframework.util.ReflectionUtils
import org.springframework.web.HttpRequestHandler
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spring.web.plugins.SpringIntegrationParametersProvider
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SpringIntegrationWebMvcRequestHandlerSpec extends Specification {

  def method = ReflectionUtils.findMethod(
      HttpRequestHandler.class,
      "handleRequest",
      HttpServletRequest.class,
      HttpServletResponse.class)

  def methodResolver = Mock(HandlerMethodResolver)
  def requestMappingInfo = RequestMappingInfo.paths("/foo").build()
  def inboundEndpoint = Mock(BaseHttpInboundEndpoint)
  def handlerMethod = Mock(HandlerMethod) {
    getBean() >> inboundEndpoint
    getMethod() >> method
  }
  def resolvedMethodParameter = Mock(ResolvedMethodParameter)
  def parametersProvider = Mock(SpringIntegrationParametersProvider) {
    getParameters(_) >> [resolvedMethodParameter]
  }

  def requestHandler = new SpringIntegrationWebMvcRequestHandler(
      "", methodResolver, requestMappingInfo, handlerMethod, parametersProvider
  )

  def "Gets Group Name"() {
    given:
    handlerMethod.getBeanType() >> WebFluxInboundEndpoint

    when:
    def groupName = requestHandler.groupName()

    then:
    groupName == "web-flux-inbound-endpoint"
  }

  def "Gets Name"() {
    given:
    inboundEndpoint.getComponentName() >> "baz"

    when:
    def name = requestHandler.getName()

    then:
    name == "baz"
  }

  def "Gets ReturnType"() {
    given:
    def resolvedType = Mock(ResolvedType)
    methodResolver.methodReturnType(_) >> resolvedType

    when:
    def returnType = requestHandler.getReturnType()

    then:
    returnType == resolvedType
  }

  def "Finds Annotation"() {
    when:
    def annotation = requestHandler.findAnnotation(Api)

    then:
    annotation.isPresent() == false
  }

  def "Gets Parameters"() {
    when:
    def parameters = requestHandler.getParameters()

    then:
    parameters.size() == 1

  }
}
