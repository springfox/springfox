package springfox.documentation.spring.web

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.condition.*
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.DocumentationContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.mixins.HandlerMethodsSupport

class OperationCachingEquivalenceSpec extends Specification implements HandlerMethodsSupport {
  def "Two request handlers backed by the same method must be equal" () {
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
          documentationContext,
          new WebMvcRequestHandler(firstMapping, anyMethod))
      def second = new RequestMappingContext(
          documentationContext,
          new WebMvcRequestHandler(secondMapping, anyMethod))
    then:
      sut.doEquivalent(first, second)
  }

  def "Two request handlers backed by different methods must NOT be equal" () {
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
        documentationContext,
        new WebMvcRequestHandler(firstMapping, anyMethod))
      def second = new RequestMappingContext(
        documentationContext,
        new WebMvcRequestHandler(secondMapping, anyMethod))
    then:
      !sut.doEquivalent(first, second)
  }

  def paths(String ... paths) {
    paths
  }

  def mediaTypes(String ... mediaTypes) {
    mediaTypes
  }

  def methods(RequestMethod ... methods) {
    methods
  }

  def requestMapping(paths, consumes, produces, methods) {
    RequestCondition custom = null
    new RequestMappingInfo("someName",
        new PatternsRequestCondition(paths),
        new RequestMethodsRequestCondition(methods),
        new ParamsRequestCondition(),
        new HeadersRequestCondition(),
        new ConsumesRequestCondition(consumes),
        new ProducesRequestCondition(produces),
        custom
      )
  }
}
