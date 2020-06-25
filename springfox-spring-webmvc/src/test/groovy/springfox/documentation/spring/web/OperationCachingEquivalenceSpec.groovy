/*
 *
 *  Copyright 2016-2019 the original author or authors.
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
import com.fasterxml.classmate.TypeResolver
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition
import org.springframework.web.servlet.mvc.condition.NameValueExpression
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition
import org.springframework.web.servlet.mvc.condition.RequestCondition
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import springfox.documentation.RequestHandler
import springfox.documentation.RequestHandlerKey
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.DocumentationContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.mixins.HandlerMethodsSupport
import springfox.documentation.spring.web.paths.Paths
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver
import springfox.documentation.spring.wrapper.PatternsRequestCondition

import java.lang.annotation.Annotation

class OperationCachingEquivalenceSpec extends Specification implements HandlerMethodsSupport {
  def methodResolver = new HandlerMethodResolver(new TypeResolver())

  def "Two request handlers backed by the same method must be equal"() {
    given:
    OperationCachingEquivalence sut = new OperationCachingEquivalence()
    def documentationContext = Mock(DocumentationContext)
    def firstMapping = requestMapping(
        paths("/a"),
        mediaTypes(MediaType.APPLICATION_JSON_VALUE),
        mediaTypes(MediaType.APPLICATION_JSON_VALUE),
        methods(RequestMethod.GET))
    def secondMapping = requestMapping(
        paths("/a"),
        mediaTypes(MediaType.APPLICATION_JSON_VALUE),
        mediaTypes(MediaType.APPLICATION_JSON_VALUE),
        methods(RequestMethod.GET))
    def anyMethod = methodWithParent()
    when:
    documentationContext.getGenericsNamingStrategy() >> Mock(GenericTypeNamingStrategy)
    and:
    def first = new RequestMappingContext(
        "0",
        documentationContext,
        new WebMvcRequestHandler(Paths.ROOT, methodResolver, firstMapping, anyMethod))
    def second = new RequestMappingContext(
        "0",
        documentationContext,
        new WebMvcRequestHandler(Paths.ROOT, methodResolver, secondMapping, anyMethod))
    then:
    sut.test(first, second)
  }

  def "One or the other is null"() {
    given:
    OperationCachingEquivalence sut = new OperationCachingEquivalence()
    def first = new RequestMappingContext(
        "0",
        Mock(DocumentationContext),
        requestHandler(firstKey))
    def second = new RequestMappingContext(
        "0",
        Mock(DocumentationContext),
        requestHandler(secondKey))
    expect:
    sut.test(first, second) == outcome
    where:
    firstKey | secondKey | outcome
    null     | null      | true
    "a"      | null      | false
    null     | "b"       | false
  }

  def "Two request handlers backed by different methods must NOT be equal"() {
    given:
    OperationCachingEquivalence sut = new OperationCachingEquivalence()
    def documentationContext = Mock(DocumentationContext)
    def firstMapping = requestMapping(
        paths("/ab"),
        mediaTypes(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE),
        mediaTypes(MediaType.APPLICATION_JSON_VALUE),
        methods(RequestMethod.PATCH))
    def secondMapping = requestMapping(
        paths("/a"),
        mediaTypes(MediaType.APPLICATION_JSON_VALUE),
        mediaTypes(MediaType.APPLICATION_JSON_VALUE),
        methods(RequestMethod.GET))
    def anyMethod = methodWithParent()
    when:
    documentationContext.getGenericsNamingStrategy() >> Mock(GenericTypeNamingStrategy)
    and:
    def first = new RequestMappingContext(
        "0",
        documentationContext,
        new WebMvcRequestHandler(
            Paths.ROOT,
            methodResolver,
            firstMapping,
            anyMethod))
    def second = new RequestMappingContext(
        "0",
        documentationContext,
        new WebMvcRequestHandler(
            Paths.ROOT,
            methodResolver,
            secondMapping,
            anyMethod))
    then:
    !sut.test(first, second)
  }

  def paths(String... paths) {
    paths
  }

  def mediaTypes(String... mediaTypes) {
    mediaTypes
  }

  def methods(RequestMethod... methods) {
    methods
  }

  def requestMapping(paths, consumes, produces, methods) {
    RequestCondition custom = null
    new RequestMappingInfo("someName",
        new org.springframework.web.servlet.mvc.condition.PatternsRequestCondition(paths),
        new RequestMethodsRequestCondition(methods),
        new ParamsRequestCondition(),
        new HeadersRequestCondition(),
        new ConsumesRequestCondition(consumes),
        new ProducesRequestCondition(produces),
        custom
    )
  }

  def requestHandler(def handlerKey) {
    new RequestHandler() {
      @Override
      Class<?> declaringClass() {
        return null
      }

      @Override
      boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
        return false
      }

      @Override
      PatternsRequestCondition getPatternsCondition() {
        return null
      }

      @Override
      String groupName() {
        return null
      }

      @Override
      String getName() {
        return null
      }

      @Override
      Set<RequestMethod> supportedMethods() {
        return null
      }

      @Override
      Set<MediaType> produces() {
        return null
      }

      @Override
      Set<MediaType> consumes() {
        return null
      }

      @Override
      Set<NameValueExpression<String>> headers() {
        return null
      }

      @Override
      Set<NameValueExpression<String>> params() {
        return null
      }

      @Override
      def <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
        return null
      }

      RequestHandlerKey key() {
        handlerKey == null ? null : new RequestHandlerKey([] as Set, [] as Set, [] as Set, [] as Set)
      }

      @Override
      List<ResolvedMethodParameter> getParameters() {
        return null
      }

      @Override
      ResolvedType getReturnType() {
        return null
      }

      @Override
      def <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
        return null
      }

      @Override
      springfox.documentation.spring.wrapper.RequestMappingInfo<?> getRequestMapping() {
        return null
      }

      @Override
      HandlerMethod getHandlerMethod() {
        return null
      }

      @Override
      RequestHandler combine(RequestHandler other) {
        return null
      }
    }
  }
}
