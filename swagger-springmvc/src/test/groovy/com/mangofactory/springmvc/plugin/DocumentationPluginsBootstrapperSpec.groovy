package com.mangofactory.springmvc.plugin

import com.mangofactory.service.model.Group
import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.mangofactory.swagger.core.SwaggerCache
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin(SpringSwaggerConfigSupport)
class DocumentationPluginsBootstrapperSpec extends Specification {
  ApplicationContext applicationContext = Mock()
  ContextRefreshedEvent contextRefreshedEvent = new ContextRefreshedEvent(applicationContext)
  PluginsManager pluginManager = Mock()
  DocumentationContext context = Mock(DocumentationContext)
  Group group = Mock(Group)
  SwaggerApiResourceListing resourceListing = Mock(SwaggerApiResourceListing)
  DocumentationPluginsBootstrapper bootstrapper = new DocumentationPluginsBootstrapper(pluginManager,  [],
          new SwaggerCache(), resourceListing, defaults(Mock(ServletContext)))

  def setup() {
    resourceListing.scan(context) >> group
    group.getGroupName() >> "default"
  }

  def "Custom plugins are sensitive to being enabled or disabled"() {
    given:
      SwaggerSpringMvcPlugin enabledPlugin = Mock(SwaggerSpringMvcPlugin)
      SwaggerSpringMvcPlugin disabledPlugin = Mock(SwaggerSpringMvcPlugin)

    when:
      enabledPlugin.isEnabled() >> true
      enabledPlugin.build(_) >> context
      disabledPlugin.isEnabled() >> false
      pluginManager.getDocumentationPluginsFor(_) >>  [enabledPlugin, disabledPlugin]

    and:
      bootstrapper.onApplicationEvent(contextRefreshedEvent)

    then:
      1 * enabledPlugin.build(_) >> context
      0 * disabledPlugin.build(_)
  }

  def "Custom plugins are configured"() {
    given:
      DocumentationPlugin plugin = Mock(DocumentationPlugin)

    when:
      pluginManager.getDocumentationPluginsFor(_) >>  [plugin]
      plugin.isEnabled() >> true
      plugin.build(_) >> context

    and:
      bootstrapper.onApplicationEvent(contextRefreshedEvent)

    then:
      1 * plugin.build(_) >> context
  }
}
