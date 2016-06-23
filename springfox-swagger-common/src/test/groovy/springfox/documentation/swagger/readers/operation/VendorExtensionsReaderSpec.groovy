package springfox.documentation.swagger.readers.operation
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.service.ObjectVendorExtension
import springfox.documentation.service.StringVendorExtension
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator

@Mixin([RequestMappingSupport])
class VendorExtensionsReaderSpec extends DocumentationContextSpec {
  def "should read from annotations"() {
    given:
      OperationContext operationContext = new OperationContext(
          new OperationBuilder(new CachingOperationNameGenerator()),
          RequestMethod.GET,
          new RequestMappingContext(context(),
              new WebMvcRequestHandler(
                  requestMappingInfo("/somePath"),
                  dummyHandlerMethod('methodWithExtensions'))), 0)
      VendorExtensionsReader sut = new VendorExtensionsReader()
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    and:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
    then:
      operation.vendorExtensions.size() == 2
      operation.vendorExtensions.first().equals(first())
      operation.vendorExtensions.subList(1, 2).first().equals(second())
  }

  def second() {
    def second = new ObjectVendorExtension("x-test2")
    second.with {
      addProperty(new StringVendorExtension("name2", "value2"))
    }
    second
  }

  def first() {
    def first = new ObjectVendorExtension("")
    first.with {
      addProperty(new StringVendorExtension("x-test1", "value1"))
    }
    first
  }
}
