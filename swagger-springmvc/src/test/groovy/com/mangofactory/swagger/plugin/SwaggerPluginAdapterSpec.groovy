package com.mangofactory.swagger.plugin

import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import spock.lang.Specification

class SwaggerPluginAdapterSpec extends Specification {

  def "default plugin creation"() {
    given:
      SpringSwaggerConfig springSwaggerConfig = Stub()
      springSwaggerConfig.defaultResourceGroupingStrategy() >> {
        throw new IllegalArgumentException("I was called")
      }

      ApplicationContext applicationContext = Mock()
      applicationContext.getBeansOfType(SwaggerSpringMvcPlugin.class) >> [:]

      ContextRefreshedEvent contextRefreshedEvent = new ContextRefreshedEvent(applicationContext)

      SwaggerPluginAdapter swaggerPluginAdapter = new SwaggerPluginAdapter(springSwaggerConfig)

    when:
      swaggerPluginAdapter.onApplicationEvent(contextRefreshedEvent)

    then:
      def e = thrown IllegalArgumentException
      e.message == "I was called"

  }

  def "Custom plugins are configured"() {
    given:
      SpringSwaggerConfig springSwaggerConfig = Stub()
      ApplicationContext applicationContext = Mock()

      SwaggerSpringMvcPlugin swaggerSpringMvcPlugin = Mock(SwaggerSpringMvcPlugin)
      applicationContext.getBeansOfType(SwaggerSpringMvcPlugin.class) >> ['plugin': swaggerSpringMvcPlugin]

      ContextRefreshedEvent contextRefreshedEvent = new ContextRefreshedEvent(applicationContext)
      SwaggerPluginAdapter swaggerPluginAdapter = new SwaggerPluginAdapter(springSwaggerConfig)

    when:
      swaggerPluginAdapter.onApplicationEvent(contextRefreshedEvent)

    then:
      1 * swaggerSpringMvcPlugin.build() >> swaggerSpringMvcPlugin
      1 * swaggerSpringMvcPlugin.initialize()
  }
}
