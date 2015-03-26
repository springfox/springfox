/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.plugins;

import com.fasterxml.classmate.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.DocumentationPlugin;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner;

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
  private final List<RequestMappingHandlerMapping> handlerMappings;
  private final DefaultConfiguration defaultConfiguration;

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
    this.defaultConfiguration
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
    return each.configure(defaultContextBuilder(each));
  }

  private void scanDocumentation(DocumentationContext context) {
    scanned.addDocumentation(resourceListing.scan(context));
  }

  private DocumentationContextBuilder defaultContextBuilder(DocumentationPlugin each) {
    DocumentationType documentationType = each.getDocumentationType();

    return documentationPluginsManager
            .createContextBuilder(documentationType, defaultConfiguration)
            .handlerMappings(handlerMappings);
  }

}
