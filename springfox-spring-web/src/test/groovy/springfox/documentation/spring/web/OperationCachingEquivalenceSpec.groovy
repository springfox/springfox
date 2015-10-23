package springfox.documentation.spring.web
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
      def reqMappingInfo = null
    when:
      documentationContext.getGenericsNamingStrategy() >> Mock(GenericTypeNamingStrategy)
    and:
      def first = new RequestMappingContext(documentationContext, reqMappingInfo, firstMethod)
      def second = new RequestMappingContext(documentationContext, reqMappingInfo, secondMethod)
    then:
      sut.doEquivalent(first, second)
    where:
      firstMethod         | secondMethod
      null                | null
      methodWithParent()  | methodWithParent()
  }

  def "Two request handlers backed by different methods must NOT be equal" () {
    given:
      OperationCachingEquivalence sut = new OperationCachingEquivalence()
      def documentationContext = Mock(DocumentationContext)
      def reqMappingInfo = null
    when:
      documentationContext.getGenericsNamingStrategy() >> Mock(GenericTypeNamingStrategy)
    and:
      def first = new RequestMappingContext(documentationContext, reqMappingInfo, firstMethod)
      def second = new RequestMappingContext(documentationContext, reqMappingInfo, secondMethod)
    then:
      !sut.doEquivalent(first, second)
    where:
      firstMethod         | secondMethod
      null                | methodWithParent()
      methodWithParent()  | null
      methodWithParent()  | methodWithChild()

  }
}
