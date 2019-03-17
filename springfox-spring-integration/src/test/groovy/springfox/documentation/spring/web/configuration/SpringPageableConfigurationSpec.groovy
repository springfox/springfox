package springfox.documentation.spring.web.configuration

import com.fasterxml.classmate.TypeResolver
import org.springframework.core.Ordered
import spock.lang.Specification

class SpringPageableConfigurationSpec extends Specification {

  def "pageable convention order is highest"() {
    given:
    def config = new SpringPageableConfiguration()

    when:
    def convention = config.pageableConvention(new TypeResolver())

    then:
    Ordered.HIGHEST_PRECEDENCE == convention.getOrder()
  }

  def "pageable convention contains pageable rule"() {
    given:
    def config = new SpringPageableConfiguration()

    when:
    def convention = config.pageableConvention(new TypeResolver())

    def rules = convention.rules()
    then:
    1 == rules.size()
  }

  def "configuration throw error if annotation are removed"() {
    given:
    def config = new SpringPageableConfigurationMissingField()

    when:
    def convention = config.pageableConvention(new TypeResolver())

    convention.rules()
    then:
    thrown IllegalStateException
  }

  class SpringPageableConfigurationMissingField extends SpringPageableConfiguration {
    private String page = "";
  }
}
