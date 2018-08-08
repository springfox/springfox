package springfox.documentation.spi.service.contexts

import spock.lang.Specification
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

import static java.util.stream.Collectors.*

class OrderingsSpec extends Specification {
  def "Orderings dont crash when docket group names are null" () {
    given:
      Docket docket1 = new Docket(DocumentationType.SPRING_WEB)
      Docket docket2 = new Docket(DocumentationType.SWAGGER_12).groupName("non-default")
      Docket docket3 = new Docket(DocumentationType.SWAGGER_12).groupName("a#1")
    when:
      def ordered = [docket1, docket2, docket3].stream().sorted(Orderings.byPluginName()).collect(toList())
    then:
      ordered[0] == docket3
      ordered[1] == docket1
      ordered[2] == docket2
  }
}
