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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

@Component
public class SchemaPluginsManager {
  private final PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propertyEnrichers;
  private final PluginRegistry<ModelBuilderPlugin, DocumentationType> modelEnrichers;

  @Autowired
  public SchemaPluginsManager(
      @Qualifier("modelPropertyBuilderPluginRegistry")
      PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propertyEnrichers,
      @Qualifier("modelBuilderPluginRegistry")
      PluginRegistry<ModelBuilderPlugin, DocumentationType> modelEnrichers) {
    this.propertyEnrichers = propertyEnrichers;
    this.modelEnrichers = modelEnrichers;
  }

  public ModelProperty property(ModelPropertyContext context) {
    for (ModelPropertyBuilderPlugin enricher : propertyEnrichers.getPluginsFor(context.getDocumentationType())) {
      enricher.apply(context);
    }
    return context.getBuilder().build();
  }

  public Model model(ModelContext context) {
    for (ModelBuilderPlugin enricher : modelEnrichers.getPluginsFor(context.getDocumentationType())) {
      enricher.apply(context);
    }
    return context.getBuilder().build();
  }

}
