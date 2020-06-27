package springfox.documentation.swagger.web

import org.springframework.mock.env.MockEnvironment
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager

class InMemorySwaggerResourcesProviderSpec extends Specification {

  @Unroll
  def "OpenAPI url when base url is #baseUrl, oasPath is #oasPath"() {
    given:
    def cache = new DocumentationCache()

    when:
    def sut = new InMemorySwaggerResourcesProvider(mockEnv(baseUrl, oasPath), cache, plugins())

    then:
    sut.oas3Url == expected

    where:
    baseUrl | oasPath | expected
    null    | null    | "/v3/api-docs"
    null    | ""      | "/"
    null    | "/"     | "/"
    null    | "/path" | "/path"
    "/"     | null    | "/v3/api-docs"
    "/"     | ""      | "/"
    "/"     | "/"     | "/"
    "/"     | "/path" | "/path"
    "/path" | null    | "/v3/api-docs"
    "/path" | ""      | "/"
    "/path" | "/"     | "/"
    "/path" | "/path" | "/path"
  }

  def mockEnv(baseUrl, oasPath) {
    MockEnvironment env = new MockEnvironment()
    if (baseUrl != null) {
      env.withProperty("springfox.documentation.swagger-ui.base-url", baseUrl)
    }
    if (oasPath != null) {
      env.withProperty("springfox.documentation.open-api.v3.path", oasPath)
    }
    env
  }

  DocumentationPluginsManager plugins() {
    def plugins = Mock(DocumentationPluginsManager)
    plugins.documentationPlugins() >> [new Docket(DocumentationType.OAS_30)]
    plugins
  }
}
