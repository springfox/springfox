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
import springfox.documentation.builders.ModelFacetsBuilder;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.property.ModelPropertiesProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.Optional.*;
import static java.util.function.Function.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.schema.Types.*;


@Component
@Qualifier("default")
public class DefaultModelSpecificationProvider implements ModelSpecificationProvider {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultModelSpecificationProvider.class);
  private final TypeResolver resolver;
  private final ModelPropertiesProvider propertiesProvider;
  private final ModelDependencyProvider dependencyProvider;
  private final SchemaPluginsManager schemaPluginsManager;
  private final TypeNameExtractor typeNameExtractor;
  private final EnumTypeDeterminer enumTypeDeterminer;

  @Autowired
  public DefaultModelSpecificationProvider(
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
  public Optional<ModelSpecification> modelSpecificationsFor(ModelContext modelContext) {
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

    Optional<ModelSpecification> syntheticModel = schemaPluginsManager.syntheticModelSpecification(modelContext);
    if (syntheticModel.isPresent()) {
      return of(schemaPluginsManager.modelSpecification(modelContext));
    }
    return reflectionBasedModel(
        modelContext,
        propertiesHost);
  }

  private Optional<ModelSpecification> reflectionBasedModel(
      ModelContext modelContext,
      ResolvedType propertiesHost) {
    Map<String, PropertySpecification> propertiesIndex
        = properties(modelContext, propertiesHost).stream()
        .collect(Collectors.toMap(PropertySpecification::getName, identity()));
    LOG.debug("Inferred {} properties. Properties found {}",
              propertiesIndex.size(),
              String.join(
                  ", ",
                  propertiesIndex.keySet()));
    return of(modelBuilder(propertiesHost,
                           new TreeMap<>(propertiesIndex),
                           modelContext));
  }

  private ModelSpecification modelBuilder(
      ResolvedType propertiesHost,
      Map<String, PropertySpecification> properties,
      ModelContext modelContext) {
    String typeName = typeNameExtractor.typeName(ModelContext.fromParent(
        modelContext,
        propertiesHost));
    modelContext.getModelSpecificationBuilder()
        .withName(typeName)
        .withCompound(new CompoundModelSpecification(
            properties.values(),
            properties.size(),
            properties.size()));
    return schemaPluginsManager.modelSpecification(modelContext);
  }

  @Override
  public Map<ResolvedType, ModelSpecification> modelDependenciesSpecifications(ModelContext modelContext) {
    Map<ResolvedType, ModelSpecification> models = new HashMap<>();
    for (ResolvedType resolvedType : dependencyProvider.dependentModels(modelContext)) {
      ModelContext parentContext = ModelContext.fromParent(
          modelContext,
          resolvedType);
      Optional<ModelSpecification> model = modelSpecificationsFor(parentContext);
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

  private Optional<ModelSpecification> mapModel(
      ModelContext mapContext,
      ResolvedType resolvedType) {
    if (isMapType(resolvedType) && !mapContext.hasSeenBefore(resolvedType)) {
      ResolvedType valueType = mapValueType(resolvedType);
      ModelContext valueContext = ModelContext.fromParent(
          mapContext,
          valueType);
      String typeName = typeNameExtractor.typeName(valueContext);
      return of(
          mapContext.getModelSpecificationBuilder()
              .withMap(new MapSpecification(
                           new ModelSpecificationBuilder(
                               String.format(
                                   "%s_%s",
                                   mapContext.getParameterId(),
                                   "String"))
                               .withScalar(new ScalarModelSpecification(ScalarType.STRING))
                              .build(),
                       new ModelSpecificationBuilder(
                           String.format(
                               "%s_%s",
                               mapContext.getParameterId(),
                               "String"))
                           .withReference(new ReferenceModelSpecification(
                               new ModelKey("", simpleQualifiedTypeName(valueType),
                                            mapContext.isReturnType())))
                           .build()))
          .withFacets(new ModelFacetsBuilder()
                          .withModelKey(new ModelKey(
                              simpleQualifiedTypeName(resolvedType),
                              typeName,
                              mapContext.isReturnType()))
                          .withTitle(typeName)
                          .withDescription("Key of type " + typeName)
                          .withNullable(false)
                          .withDeprecated(false)
                          .builder())
          .build());
    }
    return empty();
  }

  private List<PropertySpecification> properties(
      ModelContext context,
      ResolvedType propertiesHost) {
    return propertiesProvider.propertySpecificationsFor(
        propertiesHost,
        context);
  }
}
