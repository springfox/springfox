package com.mangofactory.swagger.plugin;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * After an application context refresh, builds and executes all SwaggerSpringMvcPlugin instances found in the
 * application
 * context.
 * <p/>
 * If no instances SwaggerSpringMvcPlugin are found a default one is created and executed.
 */
public class SwaggerPluginAdapter implements ApplicationListener<ContextRefreshedEvent> {
  private static final Logger log = LoggerFactory.getLogger(SwaggerPluginAdapter.class);
  private SpringSwaggerConfig springSwaggerConfig;
  private boolean initialized = false;

  @Autowired
  public SwaggerPluginAdapter(SpringSwaggerConfig springSwaggerConfig) {
    this.springSwaggerConfig = springSwaggerConfig;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    if (!initialized) {
      log.info("Context refreshed");
      ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();

      Map<String, SwaggerSpringMvcPlugin> plugins = BeanFactoryUtils.beansOfTypeIncludingAncestors(
              applicationContext,
              SwaggerSpringMvcPlugin.class);

      if (plugins.isEmpty()) {
        log.info("Did not find any SwaggerSpringMvcPlugins so creating a default one");
        new SwaggerSpringMvcPlugin(springSwaggerConfig)
                .build()
                .initialize();
      } else {
        log.info("Found custom SwaggerSpringMvcPlugins");

        for (Map.Entry<String, SwaggerSpringMvcPlugin> entry : plugins.entrySet()) {
          log.info("initializing plugin bean {}", entry.getKey());
          entry.getValue()
                  .build()
                  .initialize();
        }
      }
      initialized = true;
    } else {
      log.warn("SwaggerSpringMvcPlugin have already been initialized!");
    }
  }
}
