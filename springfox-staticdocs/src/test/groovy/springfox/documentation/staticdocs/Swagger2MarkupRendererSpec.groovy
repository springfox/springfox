package springfox.documentation.staticdocs

import io.github.robwin.markup.builder.MarkupLanguage
import spock.lang.Specification

import static springfox.documentation.staticdocs.DocumentationFormat.*

class Swagger2MarkupRendererSpec extends Specification {
  def "Coverts document format to swagger2markup MarkupLanguage" () {
    given:
      def sut = new Swagger2MarkupRenderer()
    expect:
      sut.convert(format) == expected
    where:
      format      | expected
      ASCIIDOC    | MarkupLanguage.ASCIIDOC
      MARKDOWN    | MarkupLanguage.MARKDOWN
  }

  def "When it receives unsupported document format" () {
    given:
      def sut = new Swagger2MarkupRenderer()
    when:
      sut.convert(HTML)
    then:
      thrown(UnsupportedOperationException)

  }
}
