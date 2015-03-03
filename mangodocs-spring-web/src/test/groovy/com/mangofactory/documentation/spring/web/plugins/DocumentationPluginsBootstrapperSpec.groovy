package com.mangofactory.documentation.spring.web.plugins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.service.Documentation
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.service.DocumentationPlugin
import com.mangofactory.documentation.spi.service.contexts.Defaults
import com.mangofactory.documentation.spring.web.GroupCache
import com.mangofactory.documentation.spring.web.scanners.ApiGroupScanner
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import spock.lang.Specification

import javax.servlet.ServletContext

class DocumentationPluginsBootstrapperSpec extends Specification {

  ApplicationContext applicationContext = Mock(ApplicationContext)
  DocumentationPluginsManager pluginManager = Mock(DocumentationPluginsManager)
  Documentation group = Mock(Documentation)
  ApiGroupScanner apiGroup = Mock(ApiGroupScanner)

  ContextRefreshedEvent contextRefreshedEvent = new ContextRefreshedEvent(applicationContext)
  DocumentationPluginsBootstrapper bootstrapper =
          new DocumentationPluginsBootstrapper(pluginManager,
          [],
          new GroupCache(),
          apiGroup,
          new TypeResolver(),
          new Defaults(), Mock(ServletContext))

  def setup() {
    apiGroup.scan(_) >> group
    group.getGroupName() >> "default"
  }

  def "Custom plugins are sensitive to being enabled or disabled"() {
    given:
      DocumentationConfigurer enabledPlugin = Mock(DocumentationConfigurer)
      DocumentationConfigurer disabledPlugin = Mock(DocumentationConfigurer)
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
