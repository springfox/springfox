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
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.property.ModelPropertiesProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;

@Component
@Qualifier("default")
public class DefaultModelDependencyProvider implements ModelDependencyProvider {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultModelDependencyProvider.class);
  private final TypeResolver typeResolver;
  private final ModelPropertiesProvider propertiesProvider;
  private final TypeNameExtractor nameExtractor;
  private final EnumTypeDeterminer enumTypeDeterminer;

  @Autowired
  public DefaultModelDependencyProvider(
      TypeResolver typeResolver,
      @Qualifier("cachedModelProperties") ModelPropertiesProvider propertiesProvider,
      TypeNameExtractor nameExtractor,
      EnumTypeDeterminer enumTypeDeterminer) {

    this.typeResolver = typeResolver;
    this.propertiesProvider = propertiesProvider;
    this.nameExtractor = nameExtractor;
    this.enumTypeDeterminer = enumTypeDeterminer;
  }

  @Override
  public Set<ResolvedType> dependentModels(ModelContext modelContext) {
    return from(resolvedDependencies(modelContext))
        .filter(ignorableTypes(modelContext))
        .filter(not(baseTypes(modelContext)))
        .toSet();
  }

  private Predicate<ResolvedType> baseTypes(final ModelContext modelContext) {
    return new Predicate<ResolvedType>() {
      @Override
      public boolean apply(ResolvedType resolvedType) {
        return isBaseType(ModelContext.fromParent(modelContext, resolvedType));
      }
    };
  }

  private boolean isBaseType(ModelContext modelContext) {
    String typeName = nameExtractor.typeName(modelContext);
    return Types.isBaseType(typeName);
  }

  private Predicate<ResolvedType> ignorableTypes(final ModelContext modelContext) {
    return new Predicate<ResolvedType>() {
      @Override
      public boolean apply(ResolvedType input) {
        return !modelContext.hasSeenBefore(input);
      }
    };
  }


  private List<ResolvedType> resolvedDependencies(ModelContext modelContext) {
    ResolvedType resolvedType = modelContext.alternateFor(modelContext.resolvedType(typeResolver));
    if (isBaseType(ModelContext.fromParent(modelContext, resolvedType))) {
      LOG.debug("Marking base type {} as seen", resolvedType.getSignature());
      modelContext.seen(resolvedType);
      return newArrayList();
    }
    List<ResolvedType> dependencies = newArrayList(resolvedTypeParameters(modelContext, resolvedType));
    dependencies.addAll(resolvedArrayElementType(modelContext, resolvedType));
    dependencies.addAll(resolvedPropertiesAndFields(modelContext, resolvedType));
    return dependencies;
  }

  private List<? extends ResolvedType> resolvedArrayElementType(ModelContext modelContext, ResolvedType resolvedType) {
    List<ResolvedType> parameters = newArrayList();
    if (resolvedType.isArray()) {
      ResolvedType elementType = resolvedType.getArrayElementType();
      LOG.debug("Adding type for element {}", elementType.getSignature());
      parameters.add(modelContext.alternateFor(elementType));
      LOG.debug("Recursively resolving dependencies for element {}", elementType.getSignature());
      parameters.addAll(resolvedDependencies(ModelContext.fromParent(modelContext, elementType)));
    }
    return parameters;
  }

  private List<? extends ResolvedType> resolvedTypeParameters(ModelContext modelContext, ResolvedType resolvedType) {
    List<ResolvedType> parameters = newArrayList();
    for (ResolvedType parameter : resolvedType.getTypeParameters()) {
      LOG.debug("Adding type for parameter {}", parameter.getSignature());
      parameters.add(modelContext.alternateFor(parameter));
      LOG.debug("Recursively resolving dependencies for parameter {}", parameter.getSignature());
      parameters.addAll(resolvedDependencies(ModelContext.fromParent(modelContext, parameter)));
    }
    return parameters;
  }

  protected List<ResolvedType> resolvedPropertiesAndFields(ModelContext modelContext, ResolvedType resolvedType) {
    if (modelContext.hasSeenBefore(resolvedType) || enumTypeDeterminer.isEnum(resolvedType.getErasedType())) {
      return newArrayList();
    }
    modelContext.seen(resolvedType);
    List<ResolvedType> properties = newArrayList();
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

  private FluentIterable<ModelProperty> nonTrivialProperties(ModelContext modelContext, ResolvedType resolvedType) {
    return from(propertiesFor(modelContext, resolvedType))
        .filter(not(baseProperty(modelContext)));
  }

  private Predicate<? super ModelProperty> baseProperty(final ModelContext modelContext) {
    return new Predicate<ModelProperty>() {
      @Override
      public boolean apply(ModelProperty input) {
        return isBaseType(ModelContext.fromParent(modelContext, input.getType()));
      }
    };
  }

  private List<ResolvedType> maybeFromRegularType(ModelContext modelContext, ModelProperty property) {
    if (isContainerType(property.getType()) || isMapType(property.getType())) {
      return newArrayList();
    }
    LOG.debug("Recursively resolving dependencies for type {}", resolvedTypeSignature(property.getType()).or("<null>"));
    return newArrayList(resolvedDependencies(ModelContext.fromParent(modelContext, property.getType())));
  }

  private List<ResolvedType> maybeFromCollectionElementType(ModelContext modelContext, ModelProperty property) {
    List<ResolvedType> dependencies = newArrayList();
    if (isContainerType(property.getType())) {
      ResolvedType collectionElementType = collectionElementType(property.getType());
      String resolvedTypeSignature = resolvedTypeSignature(collectionElementType).or("<null>");
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
    List<ResolvedType> dependencies = newArrayList();
    if (isMapType(property.getType())) {
      ResolvedType valueType = Maps.mapValueType(property.getType());
      String resolvedTypeSignature = resolvedTypeSignature(valueType).or("<null>");
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
