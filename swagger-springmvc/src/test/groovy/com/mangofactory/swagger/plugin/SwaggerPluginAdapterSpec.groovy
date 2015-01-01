package com.mangofactory.swagger.plugin

import com.mangofactory.service.model.Group
import com.mangofactory.service.model.builder.GroupBuilder
import com.mangofactory.springmvc.plugin.DocumentationPlugin
import com.mangofactory.springmvc.plugin.PluginsManager
import com.mangofactory.swagger.core.SwaggerCache
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import spock.lang.Specification

class SwaggerPluginAdapterSpec extends Specification {
  def "Custom plugins are sensitive to being enabled or disabled"() {
    given:
      ApplicationContext applicationContext = Mock()
      PluginsManager pluginManager = Mock()
      Group group = new GroupBuilder().withName("default").build()
      SwaggerSpringMvcPlugin enabledPlugin = Mock(SwaggerSpringMvcPlugin)
      enabledPlugin.isEnabled() >> true
      enabledPlugin.scan(_) >> group
      SwaggerSpringMvcPlugin disabledPlugin = Mock(SwaggerSpringMvcPlugin)
      disabledPlugin.isEnabled() >> false
      pluginManager.getDocumentationPluginsFor(_) >>  [enabledPlugin, disabledPlugin]

      ContextRefreshedEvent contextRefreshedEvent = new ContextRefreshedEvent(applicationContext)
      SwaggerPluginAdapter swaggerPluginAdapter = new SwaggerPluginAdapter(pluginManager, [], new SwaggerCache())

    when:
      swaggerPluginAdapter.onApplicationEvent(contextRefreshedEvent)

    then:
      1 * enabledPlugin.scan(_) >> group
      0 * disabledPlugin.scan(_)
  }

  def "Custom plugins are configured"() {
    given:
      PluginsManager pluginManager = Mock()
      ApplicationContext applicationContext = Mock()
      Group group = new GroupBuilder().withName("default").build()
      List<RequestMappingHandlerMapping> handlers = []
      DocumentationPlugin plugin = Mock(DocumentationPlugin)
      pluginManager.getDocumentationPluginsFor(_) >>  [plugin]
      plugin.isEnabled() >> true
      plugin.scan(handlers) >> group

      ContextRefreshedEvent contextRefreshedEvent = new ContextRefreshedEvent(applicationContext)
      SwaggerPluginAdapter swaggerPluginAdapter = new SwaggerPluginAdapter(pluginManager, handlers, new SwaggerCache())

    when:
      swaggerPluginAdapter.onApplicationEvent(contextRefreshedEvent)

    then:
      1 * plugin.scan(handlers) >> group
  }
}
