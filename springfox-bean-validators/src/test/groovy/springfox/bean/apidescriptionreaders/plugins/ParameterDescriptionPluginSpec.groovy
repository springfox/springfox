/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.bean.apidescriptionreaders.plugins

import com.fasterxml.classmate.ResolvedType
import org.springframework.mock.env.MockEnvironment
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.DocumentationContext
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.DescriptionResolver

import java.lang.annotation.Annotation

import static springfox.bean.apidescriptionreaders.plugins.AnnotatedMethodParamsHelperClass.*

class ParameterDescriptionPluginSpec extends Specification implements AnnotationsSupport {

  def "Plugin supports everything"() {
    given:
      def plugin = new ParameterDescriptionPlugin(new DescriptionResolver(new MockEnvironment()))
    expect:
      supported == plugin.supports(delimiter)
    where:
      supported | delimiter
      true      | DocumentationType.SWAGGER_12
      true      | DocumentationType.SWAGGER_2
      true      | DocumentationType.SPRING_WEB
      true      | new DocumentationType("Everything", "is supported")
      true      | null
  }

  @Unroll
  def "Plugin can extract annotation ApiParam"() {
    given:
      def env =new MockEnvironment()
      env.withProperty("param1", "param 1 value")
      def sut = new ParameterDescriptionPlugin(new DescriptionResolver(env))
      def paramBuilder = new ParameterBuilder()
      def context = new ParameterContext(
            resolvedParameter(annotation),
            paramBuilder,
            Mock(DocumentationContext),
            Mock(GenericTypeNamingStrategy),
            Mock(OperationContext))
    when:
      sut.apply(context)
      def param = paramBuilder.build()
    then:
      param.description == description

    where:
      annotation            | description
      apiParam(PARAM_1)     | PARAM_1
      apiParam('${param1}') | "param 1 value"
      null                  | null
  }

  def resolvedParameter(Annotation annotation) {
    new ResolvedMethodParameter(
        0,
        "",
        annotation == null ? [] : [annotation],
        Mock(ResolvedType))
  }
}
