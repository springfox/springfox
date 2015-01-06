package com.mangofactory.documentation.plugins;

import com.mangofactory.schema.ModelContext;
import com.mangofactory.service.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

@Component
public class PluginsManager {
  private final PluginRegistry<ModelPropertyEnricher, DocumentationType> propertyEnrichers;
  private final PluginRegistry<ModelEnricher, DocumentationType> modelEnrichers;

  @Autowired
  public PluginsManager(
          @Qualifier("modelPropertyEnricherRegistry")
          PluginRegistry<ModelPropertyEnricher, DocumentationType> propertyEnrichers,
          @Qualifier("modelEnricherRegistry")
          PluginRegistry<ModelEnricher, DocumentationType> modelEnrichers) {
    this.propertyEnrichers = propertyEnrichers;
    this.modelEnrichers = modelEnrichers;
  }

  public com.mangofactory.service.model.ModelProperty enrichProperty(ModelPropertyContext context) {
    for (ModelPropertyEnricher enricher : propertyEnrichers.getPluginsFor(context.getDocumentationType())) {
      enricher.enrich(context);
    }
    return context.getBuilder().build();
  }

  public Model enrichModel(ModelContext context) {
    for (ModelEnricher enricher : modelEnrichers.getPluginsFor(context.getDocumentationType())) {
      enricher.enrich(context);
    }
    return context.getBuilder().build();
  }
}
