package com.mangofactory.documentation.plugins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

@Component
public class PluginsManager {
  private final PluginRegistry<ModelPropertyEnricher, DocumentationType> propertyEnrichers;

  @Autowired
  public PluginsManager(@Qualifier("modelPropertyEnricherRegistry")
          PluginRegistry<ModelPropertyEnricher, DocumentationType> propertyEnrichers) {
    this.propertyEnrichers = propertyEnrichers;
  }

  public com.mangofactory.service.model.ModelProperty enrichProperty(ModelPropertyContext context) {
    for (ModelPropertyEnricher enricher : propertyEnrichers.getPluginsFor(context.getDocumentationType())) {
      enricher.enrich(context);
    }
    return context.getBuilder().build();
  }
}
