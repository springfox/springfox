package springfox.documentation.schema.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.contexts.ModelContext

import static java.util.Collections.emptySet

class JacksonXmlModelPluginSpec extends Specification {
  def "Should support all swagger documentation types"() {
    given:
    def sut = new JacksonXmlModelPlugin()

    expect:
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
  }

  @Unroll
  def "Xml model plugin parses #type.localName annotation as expected"() {
    given:
    def resolver = new TypeResolver()
    JacksonXmlModelPlugin sut = new JacksonXmlModelPlugin(resolver)
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
    XmlRootElementAnnotated        | "root"
  }

  class XmlNotAnnotated {
  }

  @JacksonXmlRootElement(localName = "root")
  class XmlRootElementAnnotated {
  }
}
