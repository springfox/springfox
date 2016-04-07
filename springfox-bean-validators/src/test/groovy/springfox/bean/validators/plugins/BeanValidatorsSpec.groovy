package springfox.bean.validators.plugins

import spock.lang.Specification

import javax.validation.constraints.Size

class BeanValidatorsSpec extends Specification {
  def "Cannot instantiate" () {
    when:
      new BeanValidators()
    then:
      thrown(UnsupportedOperationException)
  }

  def "When member is null annotation is absent"() {
    when:
      def annotation = BeanValidators.annotationFrom(null, Size)
    then:
      !annotation.isPresent()
  }
}
