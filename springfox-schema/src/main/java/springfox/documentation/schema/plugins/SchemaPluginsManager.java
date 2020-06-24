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

package springfox.documentation.schema.plugins;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.ViewProviderPlugin;
import springfox.documentation.spi.schema.SyntheticModelProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class SchemaPluginsManager {
  private final PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propertyEnrichers;
  private final PluginRegistry<ModelBuilderPlugin, DocumentationType> modelEnrichers;
  private final PluginRegistry<ViewProviderPlugin, DocumentationType> viewProviders;
  private final PluginRegistry<SyntheticModelProviderPlugin, ModelContext> syntheticModelProviders;

  @Autowired
  public SchemaPluginsManager(
      @Qualifier("modelPropertyBuilderPluginRegistry")
          PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propertyEnrichers,
      @Qualifier("modelBuilderPluginRegistry")
          PluginRegistry<ModelBuilderPlugin, DocumentationType> modelEnrichers,
      @Qualifier("viewProviderPluginRegistry")
          PluginRegistry<ViewProviderPlugin, DocumentationType> viewProviders,
      @Qualifier("syntheticModelProviderPluginRegistry")
          PluginRegistry<SyntheticModelProviderPlugin, ModelContext> syntheticModelProviders) {
    this.propertyEnrichers = propertyEnrichers;
    this.modelEnrichers = modelEnrichers;
    this.viewProviders = viewProviders;
    this.syntheticModelProviders = syntheticModelProviders;
  }

  /**
   * Use {@link SchemaPluginsManager#propertySpecification(ModelPropertyContext)} instead
   * @deprecated @since 3.0.0
   * @param context - property context
   * @return the model property
   */
  @Deprecated
  public springfox.documentation.schema.ModelProperty property(ModelPropertyContext context) {
    for (ModelPropertyBuilderPlugin enricher : propertyEnrichers.getPluginsFor(context.getDocumentationType())) {
      enricher.apply(context);
    }
    return context.getBuilder().build();
  }

  /**
   * Use {@link SchemaPluginsManager#modelSpecification(ModelContext)} instead
   * @deprecated @since 3.0.0
   * @param context - model context
   * @return the model
   */
  @Deprecated
  public springfox.documentation.schema.Model model(ModelContext context) {
    for (ModelBuilderPlugin enricher : modelEnrichers.getPluginsFor(context.getDocumentationType())) {
      enricher.apply(context);
    }
    return context.getBuilder().build();
  }

  public ViewProviderPlugin viewProvider(DocumentationType documentationType) {
    return viewProviders.getPluginFor(documentationType)
        .orElseThrow(() -> new IllegalStateException("No ViewProviderPlugin for " + documentationType.getName()));
  }

  /**
   * Use {@link SchemaPluginsManager#syntheticModelSpecification(ModelContext)} instead
   * @deprecated @since 3.0.0
   * @param context - model context
   * @return the model
   */
  @Deprecated
  public Optional<springfox.documentation.schema.Model> syntheticModel(ModelContext context) {
      return syntheticModelProviders.getPluginFor(context).map(plugin -> plugin.create(context));
  }

  public ModelSpecification modelSpecification(ModelContext context) {
    for (ModelBuilderPlugin enricher : modelEnrichers.getPluginsFor(context.getDocumentationType())) {
      enricher.apply(context);
    }
    return context.getModelSpecificationBuilder().build();
  }

  public Optional<ModelSpecification> syntheticModelSpecification(ModelContext context) {
    return syntheticModelProviders.getPluginFor(context)
                                  .map(p -> p.createModelSpecification(context));
  }

  /**
   * Use {@link SchemaPluginsManager#syntheticPropertySpecifications(ModelContext)} instead
   * @deprecated @since 3.0.0
   * @param context - model context
   * @return the model
   */
  @Deprecated
  public List<springfox.documentation.schema.ModelProperty> syntheticProperties(ModelContext context) {
      return syntheticModelProviders.getPluginFor(context).map(plugin -> plugin.properties(context))
          .orElseGet(ArrayList::new);
  }

  public Set<ResolvedType> dependencies(ModelContext context) {
    return syntheticModelProviders.getPluginFor(context)
                                  .map(plugin -> plugin.dependencies(context))
                                  .orElseGet(HashSet::new);
  }

  public List<PropertySpecification> syntheticPropertySpecifications(ModelContext context) {
    return syntheticModelProviders.getPluginFor(context)
                                  .map(p -> p.propertySpecifications(context))
                                  .orElse(new ArrayList<>());
  }

  public PropertySpecification propertySpecification(ModelPropertyContext modelPropertyContext) {
    for (ModelPropertyBuilderPlugin enricher :
        propertyEnrichers.getPluginsFor(modelPropertyContext.getDocumentationType())) {
      enricher.apply(modelPropertyContext);
    }
    return modelPropertyContext.getSpecificationBuilder().build();
  }
}
