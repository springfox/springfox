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

package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.property.ModelPropertiesProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static java.util.Optional.*;
import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.schema.Types.*;


@Component
@Qualifier("default")
public class DefaultModelProvider implements ModelProvider {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultModelProvider.class);
  private final TypeResolver resolver;
  private final ModelPropertiesProvider propertiesProvider;
  private final ModelDependencyProvider dependencyProvider;
  private final SchemaPluginsManager schemaPluginsManager;
  private final TypeNameExtractor typeNameExtractor;
  private final EnumTypeDeterminer enumTypeDeterminer;

  @Autowired
  public DefaultModelProvider(
      TypeResolver resolver,
      @Qualifier("cachedModelProperties") ModelPropertiesProvider propertiesProvider,
      @Qualifier("cachedModelDependencies") ModelDependencyProvider dependencyProvider,
      SchemaPluginsManager schemaPluginsManager,
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enumTypeDeterminer) {
    this.resolver = resolver;
    this.propertiesProvider = propertiesProvider;
    this.dependencyProvider = dependencyProvider;
    this.schemaPluginsManager = schemaPluginsManager;
    this.typeNameExtractor = typeNameExtractor;
    this.enumTypeDeterminer = enumTypeDeterminer;
  }

  @Override
  public Optional<Model> modelFor(ModelContext modelContext) {
    ResolvedType propertiesHost = modelContext.alternateFor(modelContext.resolvedType(resolver));

    if (isContainerType(propertiesHost)
        || isMapType(propertiesHost)
        || enumTypeDeterminer.isEnum(propertiesHost.getErasedType())
        || isBaseType(propertiesHost)
        || modelContext.hasSeenBefore(propertiesHost)) {
      LOG.debug(
          "Skipping model of type {} as its either a container type, map, enum or base type, or its already "
              + "been handled",
          resolvedTypeSignature(propertiesHost).orElse("<null>"));
      return empty();
    }

    Optional<Model> syntheticModel = schemaPluginsManager.syntheticModel(modelContext);
    if (syntheticModel.isPresent()) {
      return of(schemaPluginsManager.model(modelContext));
    }
    return reflectionBasedModel(
        modelContext,
        propertiesHost);
  }

  private Optional<Model> reflectionBasedModel(
      ModelContext modelContext,
      ResolvedType propertiesHost) {
    Map<String, ModelProperty> propertiesIndex
        = properties(
        modelContext,
        propertiesHost).stream()
                       .collect(toMap(
                           ModelProperty::getName,
                           identity()));
    LOG.debug("Inferred {} properties. Properties found {}",
              propertiesIndex.size(),
              String.join(
                  ", ",
                  propertiesIndex.keySet()));
    return of(modelBuilder(propertiesHost,
                           new TreeMap<>(propertiesIndex),
                           modelContext));
  }

  private Model modelBuilder(
      ResolvedType propertiesHost,
      Map<String, ModelProperty> properties,
      ModelContext modelContext) {
    String typeName = typeNameExtractor.typeName(ModelContext.fromParent(
        modelContext,
        propertiesHost));
    modelContext.getBuilder()
        .type(propertiesHost)
        .name(typeName)
        .qualifiedType(simpleQualifiedTypeName(propertiesHost))
        .properties(properties)
        .description("")
        .baseModel("")
        .discriminator("")
        .subTypes(new ArrayList<>());
    return schemaPluginsManager.model(modelContext);
  }

  @Override
  public Map<ResolvedType, Model> dependencies(ModelContext modelContext) {
    Map<ResolvedType, Model> models = new HashMap<>();
    for (ResolvedType resolvedType : dependencyProvider.dependentModels(modelContext)) {
      ModelContext parentContext = ModelContext.fromParent(
          modelContext,
          resolvedType);
      Optional<Model> model = modelFor(parentContext);
      if (model.isPresent()) {
        models.put(
            resolvedType,
            model.get());
      } else {
        model = mapModel(
            parentContext,
            resolvedType);
        model.ifPresent(m -> models.put(
            resolvedType,
            m));
      }
    }
    return models;
  }

  private Optional<Model> mapModel(
      ModelContext parentContext,
      ResolvedType resolvedType) {
    if (isMapType(resolvedType) && !parentContext.hasSeenBefore(resolvedType)) {
      String typeName = typeNameExtractor.typeName(parentContext);

      return Optional.of(parentContext.getBuilder()
                             .type(resolvedType)
                             .name(typeName)
                             .qualifiedType(simpleQualifiedTypeName(resolvedType))
                             .properties(new HashMap<>())
                             .description("")
                             .baseModel("")
                             .discriminator("")
                             .subTypes(new ArrayList<>())
                             .build());
    }
    return empty();
  }

  private List<ModelProperty> properties(
      ModelContext context,
      ResolvedType propertiesHost) {
    return propertiesProvider.propertiesFor(
        propertiesHost,
        context);
  }
}
