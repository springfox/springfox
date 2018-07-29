package springfox.documentation.spring.web.scanners

import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ApiDescriptionBuilder

import static springfox.documentation.spring.web.plugins.Docket.*

class ResourceGroupsSpec extends Specification {
  @Unroll
  def "belongsTo tests api descriptions #api.groupName correctly"() {
    given:
    def sut = ResourceGroups.belongsTo("test")

    expect:
    sut.test(api) == matches

    where:
    api                                | matches
    apiDescription()                   | true
    apiDescription(DEFAULT_GROUP_NAME) | false
    apiDescription("test")             | true
    apiDescription("different")        | false
  }

  def apiDescription(name) {
    new ApiDescriptionBuilder()
        .groupName(name)
        .build()
  }
}
