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

package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.property.provider.ModelPropertiesProvider;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Functions.*;
import static com.google.common.base.Strings.*;
import static com.google.common.base.Suppliers.*;
import static com.google.common.collect.Maps.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.schema.Types.*;


@Component
public class DefaultModelProvider implements ModelProvider {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultModelProvider.class);
  private final TypeResolver resolver;
  private final ModelPropertiesProvider propertiesProvider;
  private final ModelDependencyProvider dependencyProvider;
  private final SchemaPluginsManager schemaPluginsManager;
  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public DefaultModelProvider(TypeResolver resolver,
                              @Qualifier("default") ModelPropertiesProvider propertiesProvider,
                              ModelDependencyProvider dependencyProvider,
                              SchemaPluginsManager schemaPluginsManager,
                              TypeNameExtractor typeNameExtractor) {
    this.resolver = resolver;
    this.propertiesProvider = propertiesProvider;
    this.dependencyProvider = dependencyProvider;
    this.schemaPluginsManager = schemaPluginsManager;
    this.typeNameExtractor = typeNameExtractor;
  }

  @Override
  public com.google.common.base.Optional<Model> modelFor(ModelContext modelContext) {
    ResolvedType propertiesHost = modelContext.alternateFor(modelContext.resolvedType(resolver));
    if (isContainerType(propertiesHost)
        || isMapType(propertiesHost)
        || propertiesHost.getErasedType().isEnum()
        || isBaseType(Types.typeNameFor(propertiesHost.getErasedType()))
        || modelContext.hasSeenBefore(propertiesHost)) {
      LOG.debug("Skipping model of type {} as its either a container type, map, enum or base type, or its already "
          + "been handled", resolvedTypeSignature(propertiesHost).or("<null>"));
      return Optional.absent();
    }
    Map<String, ModelProperty> properties = newTreeMap();
    ImmutableMap<String, Collection<ModelProperty>> propertiesIndex
        = Multimaps.index(properties(modelContext, propertiesHost), byPropertyName()).asMap();
    LOG.debug("Inferred {} properties. Properties found {}", propertiesIndex.size(),
        Joiner.on(", ").join(propertiesIndex.keySet()));
    for (Map.Entry<String, Collection<ModelProperty>> each : propertiesIndex.entrySet()) {
      properties.put(each.getKey(), merge(each.getValue()));
    }

    return Optional.of(modelBuilder(propertiesHost, properties, modelContext));
  }

  private ModelProperty merge(Collection<ModelProperty> propertyVariants) {
    ModelProperty merged = Iterables.getFirst(propertyVariants, null);
    for (ModelProperty each : Iterables.skip(propertyVariants, 1)) {
      boolean required = Optional.fromNullable(each.isRequired()).or(false) | merged.isRequired();
      merged = new ModelProperty(defaultIfAbsent(each.getName(), merged.getName()),
          defaultIfAbsent(each.getType(), merged.getType()),
          defaultIfAbsent(emptyToNull(each.getQualifiedType()), merged.getQualifiedType()),
          each.getPosition() > 0 ? each.getPosition() : merged.getPosition(),
          required,
          each.isHidden() | merged.isHidden(),
          defaultIfAbsent(emptyToNull(each.getDescription()), merged.getDescription()),
          defaultIfAbsent(each.getAllowableValues(), merged.getAllowableValues()));
      merged.updateModelRef(forSupplier(ofInstance(defaultIfAbsent(each.getModelRef(), merged.getModelRef()))));
    }
    return merged;
  }

  private Model modelBuilder(ResolvedType propertiesHost,
                             Map<String, ModelProperty> properties,
                             ModelContext modelContext) {
    String typeName = typeNameExtractor.typeName(ModelContext.fromParent(modelContext, propertiesHost));
    modelContext.getBuilder()
        .id(typeName)
        .type(propertiesHost)
        .name(typeName)
        .qualifiedType(ResolvedTypes.simpleQualifiedTypeName(propertiesHost))
        .properties(properties)
        .description("")
        .baseModel("")
        .discriminator("")
        .subTypes(new ArrayList<String>());
    return schemaPluginsManager.model(modelContext);
  }

  @Override
  public Map<String, Model> dependencies(ModelContext modelContext) {
    Map<String, Model> models = newHashMap();
    for (ResolvedType resolvedType : dependencyProvider.dependentModels(modelContext)) {
      ModelContext parentContext = ModelContext.fromParent(modelContext, resolvedType);
      Optional<Model> model = modelFor(parentContext).or(mapModel(parentContext, resolvedType));
      if (model.isPresent()) {
        models.put(model.get().getName(), model.get());
      }
    }
    return models;
  }

  private Optional<Model> mapModel(ModelContext parentContext, ResolvedType resolvedType) {
    if (isMapType(resolvedType) && !parentContext.hasSeenBefore(resolvedType)) {
      String typeName = typeNameExtractor.typeName(parentContext);
      return Optional.of(parentContext.getBuilder()
          .id(typeName)
          .type(resolvedType)
          .name(typeName)
          .qualifiedType(ResolvedTypes.simpleQualifiedTypeName(resolvedType))
          .properties(new HashMap<String, ModelProperty>())
          .description("")
          .baseModel("")
          .discriminator("")
          .subTypes(new ArrayList<String>())
          .build());
    }
    return Optional.absent();
  }

  private Function<ModelProperty, String> byPropertyName() {
    return new Function<ModelProperty, String>() {
      @Override
      public String apply(ModelProperty input) {
        return input.getName();
      }
    };
  }

  private List<ModelProperty> properties(ModelContext context, ResolvedType propertiesHost) {
    return propertiesProvider.propertiesFor(propertiesHost, context);
  }
}
