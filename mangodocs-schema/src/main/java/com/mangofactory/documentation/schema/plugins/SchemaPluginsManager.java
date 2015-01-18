package com.mangofactory.documentation.schema.plugins;

import com.mangofactory.documentation.schema.DefaultTypeNameProvider;
import com.mangofactory.documentation.schema.ModelNameContext;
import com.mangofactory.documentation.schema.Model;
import com.mangofactory.documentation.schema.ModelProperty;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.schema.ModelBuilderPlugin;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import com.mangofactory.documentation.spi.schema.ModelPropertyBuilderPlugin;
import com.mangofactory.documentation.spi.schema.contexts.ModelPropertyContext;
import com.mangofactory.documentation.spi.schema.TypeNameProviderPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

@Component
public class SchemaPluginsManager {
  private final PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propertyEnrichers;
  private final PluginRegistry<ModelBuilderPlugin, DocumentationType> modelEnrichers;
  private final PluginRegistry<TypeNameProviderPlugin, DocumentationType> typeNameProviders;

  @Autowired
  public SchemaPluginsManager(
          @Qualifier("modelPropertyBuilderPluginRegistry")
          PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propertyEnrichers,
          @Qualifier("modelBuilderPluginRegistry")
          PluginRegistry<ModelBuilderPlugin, DocumentationType> modelEnrichers,
          @Qualifier("typeNameProviderPluginRegistry")
          PluginRegistry<TypeNameProviderPlugin, DocumentationType> typeNameProviders) {
    this.propertyEnrichers = propertyEnrichers;
    this.modelEnrichers = modelEnrichers;
    this.typeNameProviders = typeNameProviders;
  }

  public ModelProperty enrichProperty(ModelPropertyContext context) {
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

  public String typeName(ModelNameContext context) {
    TypeNameProviderPlugin selected =
            typeNameProviders.getPluginFor(context.getDocumentationType(), new DefaultTypeNameProvider());
    return selected.nameFor(context.getType());
  }
}
