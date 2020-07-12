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
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.Optional.*;
import static java.util.function.Function.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.schema.property.PackageNames.*;

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
  private final ModelSpecificationFactory modelSpecifications;

  @Autowired
  public DefaultModelSpecificationProvider(
      TypeResolver resolver,
      @Qualifier("cachedModelProperties") ModelPropertiesProvider propertiesProvider,
      @Qualifier("cachedModelDependencies") ModelDependencyProvider dependencyProvider,
      SchemaPluginsManager schemaPluginsManager,
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enumTypeDeterminer,
      ModelSpecificationFactory modelSpecifications) {
    this.resolver = resolver;
    this.propertiesProvider = propertiesProvider;
    this.dependencyProvider = dependencyProvider;
    this.schemaPluginsManager = schemaPluginsManager;
    this.typeNameExtractor = typeNameExtractor;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.modelSpecifications = modelSpecifications;
  }

  @Override
  public Optional<ModelSpecification> modelSpecificationsFor(ModelContext modelContext) {
    ResolvedType propertiesHost = modelContext.alternateEvaluatedType();

    if (isContainerType(propertiesHost)
        || isMapType(propertiesHost)
        || enumTypeDeterminer.isEnum(propertiesHost.getErasedType())
        || ScalarTypes.builtInScalarType(propertiesHost).isPresent()
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
        = properties(
        modelContext,
        propertiesHost).stream()
        .collect(Collectors.toMap(
            PropertySpecification::getName,
            identity()));
    LOG.debug(
        "Inferred {} properties. Properties found {}",
        propertiesIndex.size(),
        String.join(
            ", ",
            propertiesIndex.keySet()));
    return of(modelBuilder(
        propertiesHost,
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
        .name(typeName)
        .compoundModel(cm ->
            cm.modelKey(m ->
                m.qualifiedModelName(q ->
                    q.namespace(safeGetPackageName(propertiesHost))
                        .name(typeName))
                    .viewDiscriminator(modelContext.getView().orElse(null))
                    .validationGroupDiscriminators(modelContext.getValidationGroups())
                    .isResponse(modelContext.isReturnType())
                    .build())
                .properties(properties.values()));
    return schemaPluginsManager.modelSpecification(modelContext);
  }

  @Override
  public Set<ModelSpecification> modelDependenciesSpecifications(ModelContext modelContext) {
    Set<ModelSpecification> models = new HashSet<>();
    for (ResolvedType resolvedType : dependencyProvider.dependentModels(modelContext)) {
      ModelContext parentContext = ModelContext.fromParent(
          modelContext,
          resolvedType);
      Optional<ModelSpecification> model = modelSpecificationsFor(parentContext);
      if (model.isPresent()) {
        models.add(model.get());
      } else {
        mapModel(parentContext, resolvedType).ifPresent(models::add);
      }
    }
    return models;
  }

  private Optional<ModelSpecification> mapModel(
      ModelContext mapContext,
      ResolvedType resolvedType) {
    if (isMapType(resolvedType) && !mapContext.hasSeenBefore(resolvedType)) {
      ResolvedType keyType = resolver.resolve(String.class);
      ModelContext keyContext = ModelContext.fromParent(
          mapContext,
          keyType);
      ResolvedType valueType = mapValueType(resolvedType);
      ModelContext valueContext = ModelContext.fromParent(
          mapContext,
          valueType);
      return of(
          mapContext.getModelSpecificationBuilder()
              .mapModel(m ->
                  m.key(k ->
                      k.copyOf(modelSpecifications.create(
                          keyContext,
                          keyType)))
                      .value(v -> v.copyOf(modelSpecifications.create(
                          valueContext,
                          valueType))))
              .build());
    }
    return empty();
  }

  private List<PropertySpecification> properties(
      ModelContext context,
      ResolvedType propertiesHost) {
    String typeName = typeNameExtractor.typeName(context);
    return propertiesProvider.propertySpecificationsFor(
        propertiesHost,
        context);
  }
}
