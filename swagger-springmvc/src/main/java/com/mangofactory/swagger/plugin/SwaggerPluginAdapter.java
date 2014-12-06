package com.mangofactory.swagger.plugin;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * After an application context refresh, builds and executes all SwaggerSpringMvcPlugin instances found in the
 * application
 * context.
 * 
 * If no instances SwaggerSpringMvcPlugin are found a default one is created and executed.
 */
public class SwaggerPluginAdapter implements ApplicationListener<ContextRefreshedEvent> {
  private static final Logger log = LoggerFactory.getLogger(SwaggerPluginAdapter.class);
  private SpringSwaggerConfig springSwaggerConfig;
  private AtomicBoolean initialized = new AtomicBoolean(false);

  public SwaggerPluginAdapter(SpringSwaggerConfig springSwaggerConfig) {
    this.springSwaggerConfig = springSwaggerConfig;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    if (initialized.compareAndSet(false, true)) {
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
          if (entry.getValue().isEnabled()) {
            log.info("initializing plugin bean {}", entry.getKey());
            entry.getValue()
                    .build()
                    .initialize();
          } else {
            log.info("Skipping initializing disabled plugin bean {}", entry.getKey());
          }
        }
      }
    } else {
      log.info("Skipping SwaggerSpringMvcPlugin initialization already initialized!");
    }
  }
}
