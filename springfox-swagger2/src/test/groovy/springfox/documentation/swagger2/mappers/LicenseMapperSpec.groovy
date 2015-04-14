package springfox.documentation.swagger2.mappers

import spock.lang.Specification
import springfox.documentation.service.ApiInfo

class LicenseMapperSpec extends Specification {
  def "License is mapped from ApiInfo" () {
    given:
      def apiInfo = ApiInfo.DEFAULT
    and:
      def sut = new LicenseMapper()
    when:
      def mapped = sut.apiInfoToLicense(apiInfo)
    then:
      mapped.name == apiInfo.license
      mapped.url == apiInfo.licenseUrl
  }
}
