/*
 *
 *  Copyright 2016-2017 the original author or authors.
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
import springfox.documentation.PathProvider;
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

import java.util.List;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.spi.service.contexts.Orderings.*;

public class AbstractDocumentationPluginsBootstrapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationPluginsBootstrapper.class);
  private final DocumentationPluginsManager documentationPluginsManager;
  private final List<RequestHandlerProvider> handlerProviders;
  private final ApiDocumentationScanner resourceListing;
  private final DefaultConfiguration defaultConfiguration;
  private final DocumentationCache scanned;

  private RequestHandlerCombiner combiner;
  private List<AlternateTypeRuleConvention> typeConventions;

  public AbstractDocumentationPluginsBootstrapper(
      DocumentationPluginsManager documentationPluginsManager,
      List<RequestHandlerProvider> handlerProviders,
      DocumentationCache scanned,
      ApiDocumentationScanner resourceListing,
      Defaults defaults,
      TypeResolver typeResolver,
      PathProvider pathProvider) {
    
    this.documentationPluginsManager = documentationPluginsManager;
    this.handlerProviders = handlerProviders;
    this.scanned = scanned;
    this.resourceListing = resourceListing;
    this.defaultConfiguration = new DefaultConfiguration(defaults, typeResolver, pathProvider);
  }

  protected void bootstrapDocumentationPlugins() {
    List<DocumentationPlugin> plugins = documentationPluginsManager.documentationPlugins()
        .stream()
        .sorted(pluginOrdering())
        .collect(toList());
    LOGGER.info("Found {} custom documentation plugin(s)", plugins.size());
    for (DocumentationPlugin each : plugins) {
      DocumentationType documentationType = each.getDocumentationType();
      if (each.isEnabled()) {
        scanDocumentation(buildContext(each));
      } else {
        LOGGER.info("Skipping initializing disabled plugin bean {} v{}",
            documentationType.getName(), documentationType.getVersion());
      }
    }
  }

  protected DocumentationContext buildContext(DocumentationPlugin each) {
    return each.configure(defaultContextBuilder(each));
  }

  protected void scanDocumentation(DocumentationContext context) {
    try {
      getScanned().addDocumentation(resourceListing.scan(context));
    } catch (Exception e) {
      LOGGER.error(String.format("Unable to scan documentation context %s", context.getGroupName()), e);
    }
  }

  private DocumentationContextBuilder defaultContextBuilder(DocumentationPlugin plugin) {
    DocumentationType documentationType = plugin.getDocumentationType();
    List<RequestHandler> requestHandlers = handlerProviders.stream()
        .map(handlers())
        .flatMap((handle) -> StreamSupport.stream(handle.spliterator(), false))
        .collect(toList());
    List<AlternateTypeRule> rules = nullToEmptyList(typeConventions).stream()
        .map(AlternateTypeRuleConvention::rules)
        .flatMap((rule) -> StreamSupport.stream(rule.spliterator(), false))
        .collect(toList());
    return documentationPluginsManager
        .createContextBuilder(documentationType, defaultConfiguration)
        .rules(rules)
        .requestHandlers(combiner().combine(requestHandlers));
  }

  private RequestHandlerCombiner combiner() {
    return ofNullable(combiner).orElse(new DefaultRequestHandlerCombiner());
  }

  private Function<RequestHandlerProvider, ? extends Iterable<RequestHandler>> handlers() {
    return (Function<RequestHandlerProvider, Iterable<RequestHandler>>) RequestHandlerProvider::requestHandlers;
  }

  public void setCombiner(RequestHandlerCombiner combiner) {
    this.combiner = combiner;
  }

  public void setTypeConventions(List<AlternateTypeRuleConvention> typeConventions) {
    this.typeConventions = typeConventions;
  }

  public DocumentationPluginsManager getDocumentationPluginsManager() {
    return documentationPluginsManager;
  }

  public List<RequestHandlerProvider> getHandlerProviders() {
    return handlerProviders;
  }

  public ApiDocumentationScanner getResourceListing() {
    return resourceListing;
  }

  public DefaultConfiguration getDefaultConfiguration() {
    return defaultConfiguration;
  }

  public DocumentationCache getScanned() {
    return scanned;
  }

  public RequestHandlerCombiner getCombiner() {
    return combiner;
  }

  public List<AlternateTypeRuleConvention> getTypeConventions() {
    return typeConventions;
  }
}
