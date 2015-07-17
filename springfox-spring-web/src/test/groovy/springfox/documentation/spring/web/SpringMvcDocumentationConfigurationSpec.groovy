package springfox.documentation.spring.web

import com.google.common.base.Suppliers
import spock.lang.Specification

class SpringMvcDocumentationConfigurationSpec extends Specification {
  def "method coverage test" () {
    given:
      def config = new SpringfoxWebMvcConfiguration()
      config.modelCaches = Suppliers.ofInstance([])
    expect:
      config.with {
        defaults()
        resourceGroupCache()
        objectMapperConfigurer()
        operationsCache()
        springfoxCacheManagerSupplier()
        operationsKeyGenerator()
        jsonSerializer([])
      }
  }
}
