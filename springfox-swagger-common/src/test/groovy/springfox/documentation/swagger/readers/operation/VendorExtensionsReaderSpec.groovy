package springfox.documentation.swagger.readers.operation
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.service.ObjectVendorExtension
import springfox.documentation.service.StringVendorExtension
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport])
class VendorExtensionsReaderSpec extends DocumentationContextSpec {
  def "should read from annotations"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
        RequestMethod.GET, dummyHandlerMethod('methodWithExtensions'), 0, requestMappingInfo("somePath"),
        context(), "/anyPath")
      VendorExtensionsReader sut = new VendorExtensionsReader()
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

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
