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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
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
 * After Spring ContextRefreshedEvent, builds and executes all DocumentationConfigurer instances found in the
 * application context, because only after ContextRefreshedEvent the spring-integration handler mappings
 * are initialized.
 */
@Component
public class SpringIntegrationDocumentationPluginsBootstrapper extends AbstractDocumentationPluginsBootstrapper {

  private AtomicBoolean initialized = new AtomicBoolean(false);

  @Autowired
  public SpringIntegrationDocumentationPluginsBootstrapper(
      DocumentationPluginsManager documentationPluginsManager,
      List<RequestHandlerProvider> handlerProviders,
      DocumentationCache scanned,
      ApiDocumentationScanner resourceListing,
      TypeResolver typeResolver,
      Defaults defaults,
      PathProvider pathProvider) {
    super(documentationPluginsManager,
        handlerProviders,
        scanned,
        resourceListing,
        defaults,
        typeResolver,
        pathProvider);
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

  @EventListener(ContextRefreshedEvent.class)
  public void contextRefreshedEventExecute() {
    if (initialized.compareAndSet(false, true)) {
      super.bootstrapDocumentationPlugins();
    }
  }
}
