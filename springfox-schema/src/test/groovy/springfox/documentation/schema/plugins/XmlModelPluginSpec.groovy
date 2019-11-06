package springfox.documentation.schema.plugins

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.contexts.ModelContext

import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

import static java.util.Collections.*

class XmlModelPluginSpec extends Specification {
  def "Should support all swagger documentation types"() {
    given:
    def sut = new XmlModelPlugin()

    expect:
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
  }

  @Unroll
  def "Xml model plugin parses #type.name annotation as expected"() {
    given:
    def resolver = new TypeResolver()
    XmlModelPlugin sut = new XmlModelPlugin(resolver)
    ModelContext context = ModelContext.inputParam(
        "0_0",
        "group",
        resolver.resolve(type),
        Optional.empty(),
        new HashSet<>(),
        DocumentationType.SWAGGER_12,
        new AlternateTypeProvider([]),
        new DefaultGenericTypeNamingStrategy(),
        emptySet())
    when:
    sut.apply(context)

    then:
    context.builder.build()?.xml?.name == expected

    where:
    type                           | expected
    XmlNotAnnotated                | null
    XmlTypeAnnotated               | "type"
    XmlRootElementAnnotated        | "root"
    XmlTypeAndRootElementAnnotated | "root"
  }

  class XmlNotAnnotated {
  }

  @XmlType(name = "type")
  class XmlTypeAnnotated {
  }

  @XmlRootElement(name = "root")
  class XmlRootElementAnnotated {
  }

  @XmlType(name = "type")
  @XmlRootElement(name = "root")
  class XmlTypeAndRootElementAnnotated {
  }
}
