package springfox.documentation.swagger.readers.operation
import com.fasterxml.classmate.TypeResolver
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport])
class SwaggerOperationModelsProviderSpec extends DocumentationContextSpec {
  def "should read from annotations"() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
          [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}', '/somePath/{businessId:\\d+}')]
      )
      RequestMappingContext requestContext = new RequestMappingContext(context(), requestMappingInfo,
          dummyHandlerMethod(operationName))
      SwaggerOperationModelsProvider sut = new SwaggerOperationModelsProvider(new TypeResolver())
    when:
      sut.apply(requestContext)
      def models = requestContext.operationModelsBuilder().build()

    then:
      models.size() == modelCount
    and:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
    where:
      operationName                         | modelCount
      'dummyMethod'                         | 1
      'methodWithPosition'                  | 1
      'methodApiResponseClass'              | 2
      'methodAnnotatedWithApiResponse'      | 2
  }

}
