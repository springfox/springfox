package springdox.documentation.spring.web.plugins

import com.fasterxml.classmate.TypeResolver
import com.google.common.collect.Ordering
import spock.lang.Specification
import springdox.documentation.spi.DocumentationType
import springdox.documentation.spi.service.contexts.Defaults
import springdox.documentation.spi.service.contexts.DocumentationContextBuilder
import springdox.documentation.spring.web.readers.operation.ApiOperationReader

import javax.servlet.ServletContext

import static springdox.documentation.spi.service.contexts.Orderings.*

public class DocumentationContextSpec extends Specification {
  DocumentationContextBuilder contextBuilder
  Docket plugin
  ApiOperationReader operationReader
  private defaultConfiguration

  def setup() {
    defaultConfiguration = new DefaultConfiguration(new Defaults(), new TypeResolver(), Mock(ServletContext))

    contextBuilder = this.defaultConfiguration.create(DocumentationType.SWAGGER_12)
            .handlerMappings([])
            .operationOrdering(Ordering.from(nickNameComparator()))
    plugin = new Docket(DocumentationType.SWAGGER_12)
    operationReader = Mock(ApiOperationReader)
  }

  def context() {
    plugin.configure(contextBuilder)
  }
}
