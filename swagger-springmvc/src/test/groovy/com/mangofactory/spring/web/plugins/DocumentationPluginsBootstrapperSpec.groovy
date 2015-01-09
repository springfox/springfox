package com.mangofactory.spring.web.plugins

import com.mangofactory.schema.plugins.DocumentationType
import com.mangofactory.service.model.Group
import com.mangofactory.spring.web.scanners.ApiGroupScanner
import com.mangofactory.spring.web.GroupCache
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin(SpringSwaggerConfigSupport)
class DocumentationPluginsBootstrapperSpec extends Specification {
  ApplicationContext applicationContext = Mock()
  ContextRefreshedEvent contextRefreshedEvent = new ContextRefreshedEvent(applicationContext)
  DocumentationPluginsManager pluginManager = Mock()
  DocumentationContext context = Mock(DocumentationContext)
  Group group = Mock(Group)
  ApiGroupScanner resourceListing = Mock(ApiGroupScanner)
  DocumentationPluginsBootstrapper bootstrapper = new DocumentationPluginsBootstrapper(pluginManager,  [],
          new GroupCache(), resourceListing, defaults(Mock(ServletContext)))

  def setup() {
    resourceListing.scan(context) >> group
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
      enabledPlugin.build(_) >> context
      disabledPlugin.isEnabled() >> false
      pluginManager.documentationPlugins() >>  [enabledPlugin, disabledPlugin]

    and:
      bootstrapper.onApplicationEvent(contextRefreshedEvent)

    then:
      1 * enabledPlugin.build(_) >> context
      0 * disabledPlugin.build(_)
  }

  def "Custom plugins are configured"() {
    given:
      DocumentationPlugin plugin = Mock(DocumentationPlugin)
      plugin.documentationType >> DocumentationType.SWAGGER_12
    when:
      pluginManager.documentationPlugins() >>  [plugin]
      plugin.isEnabled() >> true
      plugin.build(_) >> context

    and:
      bootstrapper.onApplicationEvent(contextRefreshedEvent)

    then:
      1 * plugin.build(_) >> context
  }
}
