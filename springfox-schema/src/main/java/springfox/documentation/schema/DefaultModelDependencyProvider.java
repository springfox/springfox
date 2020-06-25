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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.property.ModelPropertiesProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;

@Component
@Qualifier("default")
@SuppressWarnings("deprecation")
public class DefaultModelDependencyProvider implements ModelDependencyProvider {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultModelDependencyProvider.class);
  private final TypeResolver typeResolver;
  private final ModelPropertiesProvider propertiesProvider;
  private final TypeNameExtractor nameExtractor;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final SchemaPluginsManager schemaPluginsManager;

  @Autowired
  public DefaultModelDependencyProvider(
      TypeResolver typeResolver,
      @Qualifier("cachedModelProperties") ModelPropertiesProvider propertiesProvider,
      TypeNameExtractor nameExtractor,
      EnumTypeDeterminer enumTypeDeterminer,
      SchemaPluginsManager schemaPluginsManager) {

    this.typeResolver = typeResolver;
    this.propertiesProvider = propertiesProvider;
    this.nameExtractor = nameExtractor;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.schemaPluginsManager = schemaPluginsManager;
  }

  @Override
  public Set<ResolvedType> dependentModels(ModelContext modelContext) {
    return Stream.concat(resolvedDependencies(modelContext).stream()
            .filter(ignorableTypes(modelContext))
            .filter(baseTypes(modelContext).negate()),
        schemaPluginsManager.dependencies(modelContext).stream())
        .collect(toSet());
  }

  private Predicate<ResolvedType> baseTypes(final ModelContext modelContext) {
    return resolvedType -> isBaseType(ModelContext.fromParent(modelContext, resolvedType));
  }

  private boolean isBaseType(ModelContext modelContext) {
    String typeName = nameExtractor.typeName(modelContext);
    return Types.isBaseType(typeName);
  }

  private Predicate<ResolvedType> ignorableTypes(final ModelContext modelContext) {
    return input -> !modelContext.hasSeenBefore(input);
  }


  private Set<ResolvedType> resolvedDependencies(ModelContext modelContext) {
    ResolvedType resolvedType = modelContext.alternateEvaluatedType();
    if (isBaseType(ModelContext.fromParent(modelContext, resolvedType))) {
      LOG.debug("Marking base type {} as seen", resolvedType.getSignature());
      modelContext.seen(resolvedType);
      return new HashSet<>();
    }
    Set<ResolvedType> dependencies = new HashSet<>(resolvedTypeParameters(modelContext, resolvedType));
    dependencies.addAll(resolvedArrayElementType(modelContext, resolvedType));
    dependencies.addAll(resolvedMapType(modelContext, resolvedType));
    dependencies.addAll(resolvedSubclasses(modelContext, resolvedType));
    dependencies.addAll(resolvedPropertiesAndFields(modelContext, resolvedType));
    return dependencies;
  }

  private Collection<ResolvedType> resolvedSubclasses(ModelContext modelContext, ResolvedType resolvedType) {
    JsonSubTypes subTypes = AnnotationUtils.findAnnotation(
        resolvedType.getErasedType(),
        JsonSubTypes.class);

    List<ResolvedType> subclasses = new ArrayList<ResolvedType>();
    if (subTypes != null) {
      for (JsonSubTypes.Type each : subTypes.value()) {
        ResolvedType type = typeResolver.resolve(each.value());
        subclasses.add(modelContext.alternateFor(type));
        subclasses.addAll(resolvedPropertiesAndFields(ModelContext.fromParent(modelContext, type), type));
      }
    }
    return subclasses;
  }

  private Collection<ResolvedType> resolvedMapType(ModelContext modelContext, ResolvedType resolvedType) {
    ResolvedType mapType = resolvedType.findSupertype(Map.class);
    if (mapType == null) {
      return new ArrayList<>();
    }
    return resolvedTypeParameters(modelContext, mapType);
  }

  private List<ResolvedType> resolvedArrayElementType(ModelContext modelContext, ResolvedType resolvedType) {
    List<ResolvedType> parameters = new ArrayList<>();
    if (resolvedType.isArray()) {
      ResolvedType elementType = resolvedType.getArrayElementType();
      LOG.debug("Adding type for element {}", elementType.getSignature());
      parameters.add(modelContext.alternateFor(elementType));
      LOG.debug("Recursively resolving dependencies for element {}", elementType.getSignature());
      parameters.addAll(resolvedDependencies(ModelContext.fromParent(modelContext, elementType)));
    }
    return parameters;
  }

  private Set<ResolvedType> resolvedTypeParameters(ModelContext modelContext, ResolvedType resolvedType) {
    Set<ResolvedType> parameters = new HashSet<>();
    for (ResolvedType parameter : resolvedType.getTypeParameters()) {
      LOG.debug("Adding type for parameter {}", parameter.getSignature());
      parameters.add(modelContext.alternateFor(parameter));
      LOG.debug("Recursively resolving dependencies for parameter {}", parameter.getSignature());
      parameters.addAll(resolvedDependencies(ModelContext.fromParent(modelContext, parameter)));
    }
    return parameters;
  }

  private Set<ResolvedType> resolvedPropertiesAndFields(ModelContext modelContext, ResolvedType resolvedType) {
    if (modelContext.hasSeenBefore(resolvedType) || enumTypeDeterminer.isEnum(resolvedType.getErasedType())) {
      return new HashSet<>();
    }
    modelContext.seen(resolvedType);
    HashSet<ResolvedType> properties = new HashSet<>();
    for (ModelProperty property : nonTrivialProperties(modelContext, resolvedType)) {
      LOG.debug("Adding type {} for parameter {}", property.getType().getSignature(), property.getName());
      if (!isMapType(property.getType())) {
        properties.add(property.getType());
      }
      properties.addAll(maybeFromCollectionElementType(modelContext, property));
      properties.addAll(maybeFromMapValueType(modelContext, property));
      properties.addAll(maybeFromRegularType(modelContext, property));
    }
    return properties;
  }

  private Collection<ModelProperty> nonTrivialProperties(ModelContext modelContext, ResolvedType resolvedType) {
    return propertiesFor(modelContext, resolvedType).stream()
        .filter(baseProperty(modelContext).negate()).collect(toList());
  }

  private Predicate<? super ModelProperty> baseProperty(final ModelContext modelContext) {
    return input -> isBaseType(ModelContext.fromParent(modelContext, input.getType()));
  }

  private List<ResolvedType> maybeFromRegularType(ModelContext modelContext, ModelProperty property) {
    if (isContainerType(property.getType())
        || isMapType(property.getType())) {
      return new ArrayList<>();
    }
    LOG.debug("Recursively resolving dependencies for type {}", resolvedTypeSignature(property.getType()).orElse(
        "<null>"));
    return new ArrayList<>(
        resolvedDependencies(
            ModelContext.fromParent(
                modelContext,
                property.getType())));
  }

  private List<ResolvedType> maybeFromCollectionElementType(ModelContext modelContext, ModelProperty property) {
    List<ResolvedType> dependencies = new ArrayList<>();
    if (isContainerType(property.getType())) {
      ResolvedType collectionElementType = collectionElementType(property.getType());
      String resolvedTypeSignature = resolvedTypeSignature(collectionElementType).orElse("<null>");
      if (!isBaseType(ModelContext.fromParent(modelContext, collectionElementType))) {
        LOG.debug("Adding collectionElement type {}", resolvedTypeSignature);
        dependencies.add(collectionElementType);
      }
      LOG.debug("Recursively resolving dependencies for collectionElement type {}", resolvedTypeSignature);
      dependencies.addAll(resolvedDependencies(ModelContext.fromParent(modelContext, collectionElementType)));
    }
    return dependencies;
  }

  private List<ResolvedType> maybeFromMapValueType(ModelContext modelContext, ModelProperty property) {
    List<ResolvedType> dependencies = new ArrayList<>();
    if (isMapType(property.getType())) {
      ResolvedType valueType = Maps.mapValueType(property.getType());
      String resolvedTypeSignature = resolvedTypeSignature(valueType).orElse("<null>");
      if (!isBaseType(ModelContext.fromParent(modelContext, valueType))) {
        LOG.debug("Adding value type {}", resolvedTypeSignature);
        dependencies.add(valueType);
      }
      LOG.debug("Recursively resolving dependencies for value type {}", resolvedTypeSignature);
      dependencies.addAll(resolvedDependencies(ModelContext.fromParent(modelContext, valueType)));
    }
    return dependencies;
  }

  private List<ModelProperty> propertiesFor(ModelContext modelContext, ResolvedType resolvedType) {
    return propertiesProvider.propertiesFor(resolvedType, modelContext);
  }
}
