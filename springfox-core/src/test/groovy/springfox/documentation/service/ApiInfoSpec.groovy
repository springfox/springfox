package springfox.documentation.service

import spock.lang.Specification

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
