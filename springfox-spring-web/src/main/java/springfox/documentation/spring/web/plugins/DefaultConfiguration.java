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
import springfox.documentation.PathProvider;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.DefaultsProviderPlugin;
import springfox.documentation.spi.service.contexts.ApiSelector;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;

public class DefaultConfiguration implements DefaultsProviderPlugin {

  private final Defaults defaults;
  private final TypeResolver typeResolver;
  private final PathProvider pathProvider;

  public DefaultConfiguration(
      Defaults defaults,
      TypeResolver typeResolver,
      PathProvider pathProvider) {
    this.defaults = defaults;
    this.typeResolver = typeResolver;
    this.pathProvider = pathProvider;
  }

  @Override
  public DocumentationContextBuilder create(DocumentationType documentationType) {
    return new DocumentationContextBuilder(documentationType)
        .operationOrdering(defaults.operationOrdering())
        .apiDescriptionOrdering(defaults.apiDescriptionOrdering())
        .apiListingReferenceOrdering(defaults.apiListingReferenceOrdering())
        .additionalIgnorableTypes(defaults.defaultIgnorableParameterTypes())
        .rules(defaults.defaultRules(typeResolver))
        .defaultResponseMessages(defaults.defaultResponseMessages())
        .defaultResponses(defaults.defaultResponses())
        .pathProvider(pathProvider)
        .typeResolver(typeResolver)
        .enableUrlTemplating(false)
        .selector(ApiSelector.DEFAULT);
  }

  @Override
  public DocumentationContextBuilder apply(DocumentationContextBuilder builder) {
    return builder.operationOrdering(defaults.operationOrdering())
        .apiDescriptionOrdering(defaults.apiDescriptionOrdering())
        .apiListingReferenceOrdering(defaults.apiListingReferenceOrdering())
        .additionalIgnorableTypes(defaults.defaultIgnorableParameterTypes())
        .rules(defaults.defaultRules(typeResolver))
        .defaultResponses(defaults.defaultResponses())
        .pathProvider(pathProvider)
        .typeResolver(typeResolver)
        .enableUrlTemplating(false)
        .selector(ApiSelector.DEFAULT);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
