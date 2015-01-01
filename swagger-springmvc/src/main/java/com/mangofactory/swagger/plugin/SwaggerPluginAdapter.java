package com.mangofactory.swagger.plugin;

import com.mangofactory.service.model.Group;
import com.mangofactory.springmvc.plugin.DocumentationPlugin;
import com.mangofactory.springmvc.plugin.DocumentationType;
import com.mangofactory.springmvc.plugin.PluginsManager;
import com.mangofactory.swagger.core.SwaggerCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * After an application context refresh, builds and executes all SwaggerSpringMvcPlugin instances found in the
 * application
 * context.
 *
 * If no instances SwaggerSpringMvcPlugin are found a default one is created and executed.
 */
@Component
public class SwaggerPluginAdapter implements ApplicationListener<ContextRefreshedEvent> {
  private static final Logger log = LoggerFactory.getLogger(SwaggerPluginAdapter.class);
  private final PluginsManager pluginsManager;
  private List<RequestMappingHandlerMapping> handlerMappings;
  private final SwaggerCache scanned;
  private AtomicBoolean initialized = new AtomicBoolean(false);


  @Autowired
  public SwaggerPluginAdapter(PluginsManager pluginsManager,
                              List<RequestMappingHandlerMapping> handlerMappings,
                              SwaggerCache scanned) {
    this.pluginsManager = pluginsManager;
    this.handlerMappings = handlerMappings;
    this.scanned = scanned;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    if (initialized.compareAndSet(false, true)) {
      log.info("Context refreshed");
      DocumentationType swagger = new DocumentationType("swagger", "1.2");
      List<DocumentationPlugin> plugins = pluginsManager.getDocumentationPluginsFor(swagger);
      log.info("Found custom SwaggerSpringMvcPlugins");

      for (DocumentationPlugin each : plugins) {
        if (each.isEnabled()) {
          Group group = each.scan(handlerMappings);
          scanned.addGroup(group);
        } else {
          log.info("Skipping initializing disabled plugin bean {}", each.getName());
        }
      }
    }
  }

}
