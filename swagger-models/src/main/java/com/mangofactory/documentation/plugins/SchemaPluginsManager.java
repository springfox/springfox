package com.mangofactory.documentation.plugins;

import com.mangofactory.schema.ModelContext;
import com.mangofactory.service.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

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

  public com.mangofactory.service.model.ModelProperty enrichProperty(ModelPropertyContext context) {
    for (ModelPropertyBuilderPlugin enricher : propertyEnrichers.getPluginsFor(context.getDocumentationType())) {
      enricher.apply(context);
    }
    return context.getBuilder().build();
  }

  public Model enrichModel(ModelContext context) {
    for (ModelBuilderPlugin enricher : modelEnrichers.getPluginsFor(context.getDocumentationType())) {
      enricher.apply(context);
    }
    return context.getBuilder().build();
  }
}
