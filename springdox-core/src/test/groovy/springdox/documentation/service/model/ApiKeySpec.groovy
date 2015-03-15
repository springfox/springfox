package springdox.documentation.service.model

import spock.lang.Specification
import springdox.documentation.service.ApiKey

class ApiKeySpec extends Specification {
  def "Bean properties are set as expected via constructor" () {
    when:
      def apiKey = new ApiKey("mykey", "key1", "header")
    then:
      apiKey.type == "apiKey"
    and:
      apiKey.keyname == "key1"
      apiKey.name == "mykey"
      apiKey.passAs == "header"
  }
}
