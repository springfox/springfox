package springfox.documentation.spring.web.plugins

import com.fasterxml.classmate.TypeResolver
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import spock.lang.Specification
import springfox.documentation.service.Documentation
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.DocumentationPlugin
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner

import javax.servlet.ServletContext

class DocumentationPluginsBootstrapperSpec extends Specification {

  ApplicationContext applicationContext = Mock(ApplicationContext)
  DocumentationPluginsManager pluginManager = Mock(DocumentationPluginsManager)
  Documentation group = Mock(Documentation)
  ApiDocumentationScanner apiGroup = Mock(ApiDocumentationScanner)

  ContextRefreshedEvent contextRefreshedEvent = new ContextRefreshedEvent(applicationContext)
  DocumentationPluginsBootstrapper bootstrapper =
          new DocumentationPluginsBootstrapper(pluginManager,
          [],
          new DocumentationCache(),
          apiGroup,
          new TypeResolver(),
          new Defaults(), Mock(ServletContext))

  def setup() {
    pluginManager.createContextBuilder(_, _) >> new DocumentationContextBuilder(DocumentationType.SWAGGER_12)
    apiGroup.scan(_) >> group
    group.getGroupName() >> "default"
  }

  def "Custom plugins are sensitive to being enabled or disabled"() {
    given:
      Docket enabledPlugin = Mock(Docket)
      Docket disabledPlugin = Mock(Docket)
    and:
      enabledPlugin.documentationType >> DocumentationType.SWAGGER_12
      disabledPlugin.documentationType >> DocumentationType.SWAGGER_12
    when:
      enabledPlugin.isEnabled() >> true
      disabledPlugin.isEnabled() >> false
      pluginManager.documentationPlugins() >>  [enabledPlugin, disabledPlugin]

    and:
      bootstrapper.onApplicationEvent(contextRefreshedEvent)

    then:
      1 * enabledPlugin.configure(_)
      0 * disabledPlugin.configure(_)
  }

  def "Custom plugins are configured"() {
    given:
      DocumentationPlugin plugin = Mock(DocumentationPlugin)
      plugin.documentationType >> DocumentationType.SWAGGER_12
    when:
      pluginManager.documentationPlugins() >>  [plugin]
      plugin.isEnabled() >> true

    and:
      bootstrapper.onApplicationEvent(contextRefreshedEvent)

    then:
      1 * plugin.configure(_)
  }
}
