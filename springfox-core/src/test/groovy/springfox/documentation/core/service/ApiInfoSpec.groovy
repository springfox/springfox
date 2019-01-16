package springfox.documentation.core.service

import spock.lang.Specification
import springfox.documentation.core.service.ApiInfo

class ApiInfoSpec extends Specification {
  def "deprecated constructor delegates to the new constructor" () {
    given:
      def apiInfo = new ApiInfo("title", "description", "version", "tosUrl", "Contact Name", "license", "licenseUrl")
    expect:
      apiInfo.contact.name == "Contact Name"
      apiInfo.contact.email == ""
      apiInfo.contact.url == ""
  }
}
