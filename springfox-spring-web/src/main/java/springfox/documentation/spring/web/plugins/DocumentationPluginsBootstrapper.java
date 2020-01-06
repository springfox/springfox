/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import springfox.documentation.PathProvider;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.spi.service.RequestHandlerCombiner;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Builds and executes all DocumentationConfigurer instances found in the
 * application context, at the end of all {@link #getPhase phases} in {@link SmartLifecycle} .
 * <p>
 * If no instances DocumentationConfigurer are found a default one is created and executed.
 */
@Component
@Conditional(SpringIntegrationPluginNotPresentInClassPathCondition.class)
public class DocumentationPluginsBootstrapper
    extends AbstractDocumentationPluginsBootstrapper
    implements SmartLifecycle {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationPluginsBootstrapper.class);
  private static final String SPRINGFOX_DOCUMENTATION_AUTO_STARTUP = "springfox.documentation.auto-startup";
  private final Environment environment;

  private AtomicBoolean initialized = new AtomicBoolean(false);

  @Autowired
  @SuppressWarnings("ParameterNumber")
  public DocumentationPluginsBootstrapper(
      DocumentationPluginsManager documentationPluginsManager,
      List<RequestHandlerProvider> handlerProviders,
      DocumentationCache scanned,
      ApiDocumentationScanner resourceListing,
      TypeResolver typeResolver,
      Defaults defaults,
      PathProvider pathProvider,
      Environment environment) {
    super(documentationPluginsManager, handlerProviders, scanned, resourceListing, defaults, typeResolver,
        pathProvider);

    this.environment = environment;
  }

  @Override
  public boolean isAutoStartup() {
    String autoStartupConfig =
        environment.getProperty(
            SPRINGFOX_DOCUMENTATION_AUTO_STARTUP,
            "true");
    return Boolean.valueOf(autoStartupConfig);
  }

  @Override
  public void stop(Runnable callback) {
    callback.run();
  }

  @Override
  public void start() {
    if (initialized.compareAndSet(false, true)) {
      LOGGER.info("Documentation plugins bootstrapped");
      super.bootstrapDocumentationPlugins();
    }
  }

  @Override
  public void stop() {
    initialized.getAndSet(false);
    getScanned().clear();
  }

  @Override
  public boolean isRunning() {
    return initialized.get();
  }

  @Override
  public int getPhase() {
    return Integer.MAX_VALUE;
  }

  @Override
  @Autowired(required = false)
  public void setCombiner(RequestHandlerCombiner combiner) {
    super.setCombiner(combiner);
  }

  @Override
  @Autowired(required = false)
  public void setTypeConventions(List<AlternateTypeRuleConvention> typeConventions) {
    super.setTypeConventions(typeConventions);
  }
}
