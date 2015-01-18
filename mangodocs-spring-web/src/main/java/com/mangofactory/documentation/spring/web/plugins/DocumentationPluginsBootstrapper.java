package com.mangofactory.documentation.spring.web.plugins;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.contexts.Defaults;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder;
import com.mangofactory.documentation.spi.service.DocumentationPlugin;
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy;
import com.mangofactory.documentation.spring.web.GroupCache;
import com.mangofactory.documentation.spring.web.RelativePathProvider;
import com.mangofactory.documentation.spring.web.scanners.ApiGroupScanner;
import com.mangofactory.documentation.spring.web.scanners.RegexRequestMappingPatternMatcher;
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
  private final GroupCache scanned;
  private final ApiGroupScanner resourceListing;
  private final List<RequestMappingHandlerMapping> handlerMappings;
  private final Defaults defaults;
  private final ServletContext servletContext;
  private AtomicBoolean initialized = new AtomicBoolean(false);
  private final TypeResolver typeResolver;

  @Autowired
  public DocumentationPluginsBootstrapper(DocumentationPluginsManager documentationPluginsManager,
        List<RequestMappingHandlerMapping> handlerMappings,
        GroupCache scanned,
        ApiGroupScanner resourceListing,
        TypeResolver typeResolver,
        Defaults defaults,
        ServletContext servletContext) {

    this.documentationPluginsManager = documentationPluginsManager;
    this.handlerMappings = handlerMappings;
    this.scanned = scanned;
    this.resourceListing = resourceListing;
    this.typeResolver = typeResolver;
    this.defaults = defaults;
    this.servletContext = servletContext;
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
            .typeResolver(typeResolver)
            .pathProvider(new RelativePathProvider(servletContext))
            .requestMappingPatternMatcher(new RegexRequestMappingPatternMatcher())
            .withResourceGroupingStrategy(resourceGroupingStrategy);

    each.configure(contextBuilder);
    scanned.addGroup(resourceListing.scan(contextBuilder.build()));
  }

}
