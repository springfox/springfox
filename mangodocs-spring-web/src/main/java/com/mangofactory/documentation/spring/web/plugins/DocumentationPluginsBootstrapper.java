package com.mangofactory.documentation.spring.web.plugins;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.DocumentationPlugin;
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy;
import com.mangofactory.documentation.spi.service.contexts.Defaults;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContext;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder;
import com.mangofactory.documentation.spring.web.DocumentationCache;
import com.mangofactory.documentation.spring.web.scanners.ApiDocumentationScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
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
  private final DocumentationCache scanned;
  private final ApiDocumentationScanner resourceListing;
  private final DefaultConfiguration defaultConfigurer;
  private final List<RequestMappingHandlerMapping> handlerMappings;

  private AtomicBoolean initialized = new AtomicBoolean(false);

  @Autowired
  public DocumentationPluginsBootstrapper(DocumentationPluginsManager documentationPluginsManager,
        List<RequestMappingHandlerMapping> handlerMappings,
        DocumentationCache scanned,
        ApiDocumentationScanner resourceListing,
        TypeResolver typeResolver,
        Defaults defaults,
        ServletContext servletContext) {

    this.documentationPluginsManager = documentationPluginsManager;
    this.scanned = scanned;
    this.resourceListing = resourceListing;
    this.handlerMappings = handlerMappings;
    this.defaultConfigurer
            = new DefaultConfiguration(defaults, typeResolver, servletContext);
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
          scanDocumentation(buildContext(each));
        } else {
          log.info("Skipping initializing disabled plugin bean {} v{}",
                  documentationType.getName(), documentationType.getVersion());
        }
      }
    }
  }

  private DocumentationContext buildContext(DocumentationPlugin each) {
    DocumentationContextBuilder contextBuilder = defaultContextBuilder(each);
    return each.configure(contextBuilder);
  }

  private void scanDocumentation(DocumentationContext context) {
    scanned.addDocumentation(resourceListing.scan(context));
  }

  private DocumentationContextBuilder defaultContextBuilder(DocumentationPlugin each) {
    DocumentationType documentationType = each.getDocumentationType();

    ResourceGroupingStrategy resourceGroupingStrategy
            = documentationPluginsManager.resourceGroupingStrategy(documentationType);
    DocumentationContextBuilder contextBuilder = new DocumentationContextBuilder();
    defaultConfigurer.configure(contextBuilder);
    contextBuilder
            .withResourceGroupingStrategy(resourceGroupingStrategy)
            .handlerMappings(handlerMappings);
    return contextBuilder;
  }

}
