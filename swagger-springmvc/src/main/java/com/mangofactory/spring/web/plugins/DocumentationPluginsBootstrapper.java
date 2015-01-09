package com.mangofactory.spring.web.plugins;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.spring.web.GroupCache;
import com.mangofactory.spring.web.ResourceGroupingStrategy;
import com.mangofactory.spring.web.scanners.ApiGroupScanner;
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
 * After an application context refresh, builds and executes all DocumentationConfigurer instances found in the
 * application
 * context.
 *
 * If no instances DocumentationConfigurer are found a default one is created and executed.
 */
@Component
public class DocumentationPluginsBootstrapper implements ApplicationListener<ContextRefreshedEvent> {
  private static final Logger log = LoggerFactory.getLogger(DocumentationPluginsBootstrapper.class);
  private final DocumentationPluginsManager documentationPluginsManager;
  private final GroupCache scanned;
  private final ApiGroupScanner resourceListing;
  private final List<RequestMappingHandlerMapping> handlerMappings;
  private final Defaults defaults;
  private AtomicBoolean initialized = new AtomicBoolean(false);

  @Autowired
  public DocumentationPluginsBootstrapper(DocumentationPluginsManager documentationPluginsManager,
      List<RequestMappingHandlerMapping> handlerMappings,
      GroupCache scanned,
      ApiGroupScanner resourceListing,
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
      List<DocumentationPlugin> plugins = documentationPluginsManager.documentationPlugins();
      log.info("Found {0} custom documentation plugin(s)", plugins.size());
      for (DocumentationPlugin each : plugins) {
        DocumentationType documentationType = each.getDocumentationType();
        if (each.isEnabled()) {
          scanDocumentation(each);
        } else {
          log.info("Skipping initializing disabled plugin bean {} v{}",
                  documentationType.getName(), documentationType.getVersion());
        }
      }
    }
  }

  private void scanDocumentation(DocumentationPlugin each) {
    DocumentationType documentationType = each.getDocumentationType();
    ResourceGroupingStrategy resourceGroupingStrategy
            = documentationPluginsManager.resourceGroupingStrategy(documentationType);

    DocumentationContextBuilder contextBuilder = new DocumentationContextBuilder(defaults)
            .withDocumentationType(documentationType)
            .withHandlerMappings(handlerMappings)
            .withResourceGroupingStrategy(resourceGroupingStrategy);
    scanned.addGroup(resourceListing.scan(each.build(contextBuilder)));
  }

}
