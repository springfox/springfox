package springdox.documentation.schema.configuration

import spock.lang.Specification

class ModelsConfigurationSpec extends Specification {
  def "test to include this class in the converage report" () {
    when:
      def config = new ModelsConfiguration()
    then:
      config.typeResolver() != null

  }
}
