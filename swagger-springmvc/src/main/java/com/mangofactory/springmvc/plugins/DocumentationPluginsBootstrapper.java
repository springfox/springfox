package com.mangofactory.springmvc.plugins;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.swagger.controllers.Defaults;
import com.mangofactory.swagger.core.SwaggerApiResourceListing;
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
public class DocumentationPluginsBootstrapper implements ApplicationListener<ContextRefreshedEvent> {
  private static final Logger log = LoggerFactory.getLogger(DocumentationPluginsBootstrapper.class);
  private final DocumentationPluginsManager documentationPluginsManager;
  private final SwaggerCache scanned;
  private final SwaggerApiResourceListing resourceListing;
  private final List<RequestMappingHandlerMapping> handlerMappings;
  private AtomicBoolean initialized = new AtomicBoolean(false);
  private final Defaults defaults;


  @Autowired
  public DocumentationPluginsBootstrapper(DocumentationPluginsManager documentationPluginsManager,
      List<RequestMappingHandlerMapping> handlerMappings,
      SwaggerCache scanned,
      SwaggerApiResourceListing resourceListing,
      Defaults defaults) {

    this.documentationPluginsManager = documentationPluginsManager;
    this.handlerMappings = handlerMappings;
    this.scanned = scanned;
    this.resourceListing = resourceListing;
    this.defaults = defaults;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    if (initialized.compareAndSet(false, true)) {
      log.info("Context refreshed");
      DocumentationType swagger = new DocumentationType("swagger", "1.2"); //DK TODO: Need to figure out how to get this
      // from the @EnableXXXX annotations
      List<DocumentationPlugin> plugins = documentationPluginsManager.getDocumentationPluginsFor(swagger);
      log.info("Found custom SwaggerSpringMvcPlugins");

      DocumentationContextBuilder contextBuilder = new DocumentationContextBuilder(defaults)
              .withDocumentationType(swagger)
              .withHandlerMappings(handlerMappings);
      for (DocumentationPlugin each : plugins) {
        if (each.isEnabled()) {
          scanned.addGroup(resourceListing.scan(each.build(contextBuilder)));
        } else {
          log.info("Skipping initializing disabled plugin bean {}", each.getName());
        }
      }
    }
  }

}
