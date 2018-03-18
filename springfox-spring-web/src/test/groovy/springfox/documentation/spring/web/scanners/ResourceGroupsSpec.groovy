package springfox.documentation.spring.web.scanners

import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ApiDescriptionBuilder
import springfox.documentation.service.ResourceGroup

import static springfox.documentation.spring.web.plugins.Docket.*

class ResourceGroupsSpec extends Specification {
  @Unroll
  def "belongsTo tests api descriptions #api.groupName correctly"() {
    given:
    def sut = ResourceGroups.belongsTo(resourceGroup)

    expect:
    sut.apply(api) == matches

    where:
    resourceGroup | api                                | matches
    group("test") | apiDescription()                   | true
    group("test") | apiDescription(DEFAULT_GROUP_NAME) | false
    group("test") | apiDescription("test")             | true
    group("test") | apiDescription("different")        | false
  }

  def group(name) {
    new ResourceGroup(name, null)
  }

  def apiDescription(name) {
    new ApiDescriptionBuilder()
        .groupName(name)
        .build()
  }
}
