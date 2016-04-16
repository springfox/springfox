package springfox.documentation.swagger2.mappers

import spock.lang.Specification
import springfox.documentation.builders.ApiInfoBuilder
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

  def "No license is mapped when ApiInfo contains no license info" () {
    given:
      def apiInfo = new ApiInfoBuilder().title("Api Documentation").version("1.0").build();
    and:
      def sut = new LicenseMapper()
    when:
      def mapped = sut.apiInfoToLicense(apiInfo)
    then:
      mapped == null
  }

  def "License is mapped when ApiInfo contains license url only" () {
    given:
      def apiInfo = new ApiInfoBuilder().title("Api Documentation").version("1.0")
        .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0").build();
    and:
      def sut = new LicenseMapper()
    when:
      def mapped = sut.apiInfoToLicense(apiInfo)
    then:
      mapped.url == apiInfo.licenseUrl
  }
}
