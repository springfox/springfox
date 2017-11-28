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
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import springfox.documentation.RequestHandler;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.DocumentationPlugin;
import springfox.documentation.spi.service.RequestHandlerCombiner;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.collect.FluentIterable.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.spi.service.contexts.Orderings.*;

/**
 * After an application context refresh, builds and executes all DocumentationConfigurer instances found in the
 * application context.
 *
 * If no instances DocumentationConfigurer are found a default one is created and executed.
 */
@Component
public class DocumentationPluginsBootstrapper implements SmartLifecycle {
  private static final Logger log = LoggerFactory.getLogger(DocumentationPluginsBootstrapper.class);
  private final DocumentationPluginsManager documentationPluginsManager;
  private final List<RequestHandlerProvider> handlerProviders;
  private final DocumentationCache scanned;
  private final ApiDocumentationScanner resourceListing;
  private final DefaultConfiguration defaultConfiguration;

  private AtomicBoolean initialized = new AtomicBoolean(false);

  @Autowired(required = false)
  private RequestHandlerCombiner combiner;
  @Autowired(required = false)
  private List<AlternateTypeRuleConvention> typeConventions;

  @Autowired
  public DocumentationPluginsBootstrapper(
      DocumentationPluginsManager documentationPluginsManager,
      List<RequestHandlerProvider> handlerProviders,
      DocumentationCache scanned,
      ApiDocumentationScanner resourceListing,
      TypeResolver typeResolver,
      Defaults defaults,
      ServletContext servletContext) {

    this.documentationPluginsManager = documentationPluginsManager;
    this.handlerProviders = handlerProviders;
    this.scanned = scanned;
    this.resourceListing = resourceListing;
    this.defaultConfiguration = new DefaultConfiguration(defaults, typeResolver, servletContext);
  }

  private DocumentationContext buildContext(DocumentationPlugin each) {
    return each.configure(defaultContextBuilder(each));
  }

  private void scanDocumentation(DocumentationContext context) {
    try {
      scanned.addDocumentation(resourceListing.scan(context));
    } catch (Exception e) {
      log.error(String.format("Unable to scan documentation context %s", context.getGroupName()), e);
    }
  }

  private DocumentationContextBuilder defaultContextBuilder(DocumentationPlugin plugin) {
    DocumentationType documentationType = plugin.getDocumentationType();
    List<RequestHandler> requestHandlers = from(handlerProviders)
        .transformAndConcat(handlers())
        .toList();
    List<AlternateTypeRule> rules = from(nullToEmptyList(typeConventions))
          .transformAndConcat(toRules())
          .toList();
    return documentationPluginsManager
        .createContextBuilder(documentationType, defaultConfiguration)
        .rules(rules)
        .requestHandlers(combiner().combine(requestHandlers));
  }

  private Function<AlternateTypeRuleConvention, List<AlternateTypeRule>> toRules() {
    return new Function<AlternateTypeRuleConvention, List<AlternateTypeRule>>() {
      @Override
      public List<AlternateTypeRule> apply(AlternateTypeRuleConvention input) {
        return input.rules();
      }
    };
  }

  private RequestHandlerCombiner combiner() {
    return Optional.fromNullable(combiner).or(new DefaultRequestHandlerCombiner());
  }

  private Function<RequestHandlerProvider, ? extends Iterable<RequestHandler>> handlers() {
    return new Function<RequestHandlerProvider, Iterable<RequestHandler>>() {
      @Override
      public Iterable<RequestHandler> apply(RequestHandlerProvider input) {
        return input.requestHandlers();
      }
    };
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void stop(Runnable callback) {
    callback.run();
  }

  @Override
  public void start() {
    if (initialized.compareAndSet(false, true)) {
      log.info("Context refreshed");
      List<DocumentationPlugin> plugins = pluginOrdering()
          .sortedCopy(documentationPluginsManager.documentationPlugins());
      log.info("Found {} custom documentation plugin(s)", plugins.size());
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

  @Override
  public void stop() {
    initialized.getAndSet(false);
    scanned.clear();
  }

  @Override
  public boolean isRunning() {
    return initialized.get();
  }

  @Override
  public int getPhase() {
    return Integer.MAX_VALUE;
  }
}
