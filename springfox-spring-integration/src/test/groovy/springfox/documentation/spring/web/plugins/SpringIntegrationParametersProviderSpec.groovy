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
package springfox.documentation.spring.web.plugins

import org.springframework.core.ResolvableType
import org.springframework.expression.Expression
import org.springframework.integration.http.inbound.BaseHttpInboundEndpoint
import org.springframework.integration.http.inbound.RequestMapping
import spock.lang.Specification

class SpringIntegrationParametersProviderSpec extends Specification {

  class Foo {
    def bar
  }

  def "Determines parameters"() {

    Expression payloadExpression = Mock() {
      getExpressionString() >> "#requestParams['toConvert'][0]"
    }
    Expression headerExpression = Mock() {
      getExpressionString() >> "#pathVariables.upperLower"
    }
    def headerExpressions = ["upperLower": headerExpression]

    def provider = new SpringIntegrationParametersProvider()

    given:
    def inboundEndpoint = new BaseHttpInboundEndpoint(true)
    def requestMapping = new RequestMapping()
    requestMapping.setPathPatterns("/conversions/pathvariable/{upperLower}")
    inboundEndpoint.setHeaderExpressions(headerExpressions)
    inboundEndpoint.setPayloadExpression(payloadExpression)
    inboundEndpoint.setRequestMapping(requestMapping)
    inboundEndpoint.setRequestPayloadType(ResolvableType.forClass(Foo))

    when:
    def parameters = provider.getParameters(inboundEndpoint)

    then:
    parameters.size() == 3
    parameters[0].defaultName().get() == "body"
    parameters[1].defaultName().get() == "upperLower"
    parameters[2].defaultName().get() == "toConvert"
  }
}
