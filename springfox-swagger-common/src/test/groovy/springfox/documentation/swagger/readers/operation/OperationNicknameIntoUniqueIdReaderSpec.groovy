package springfox.documentation.swagger.readers.operation
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator

@Mixin([RequestMappingSupport])
class OperationNicknameIntoUniqueIdReaderSpec extends DocumentationContextSpec {
  def "should set various unique operation id based on swagger annotation"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(new CachingOperationNameGenerator()),
        RequestMethod.GET, handlerMethod, 0, requestMappingInfo("somePath"),
        context(), "/anyPath")
    and:
      def sut = new OperationNicknameIntoUniqueIdReader()
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      operation.uniqueId == expected
    and:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
    where:
      handlerMethod                                 | expected
      dummyHandlerMethod('methodWithHttpGETMethod') | 'nullUsingGET'
      dummyHandlerMethod('methodWithNickName')      | 'uniqueUsingGET'
  }
}
