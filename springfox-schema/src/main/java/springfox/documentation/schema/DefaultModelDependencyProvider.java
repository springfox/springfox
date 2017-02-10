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
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.property.ModelPropertiesProvider;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Maps.uniqueIndex;
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

  @Autowired
  public DefaultModelDependencyProvider(
      TypeResolver typeResolver,
      @Qualifier("optimized") ModelPropertiesProvider propertiesProvider,
      TypeNameExtractor nameExtractor) {

    this.typeResolver = typeResolver;
    this.propertiesProvider = propertiesProvider;
    this.nameExtractor = nameExtractor;
  }

  @Override
  public List<ModelContext> dependentModels(ModelContext modelContext) {
    return from(resolvedDependencies(modelContext))
        .filter(ignorableTypes())
        .filter(not(baseTypes(modelContext)))
        .toList();
  }

  private Predicate<ModelContext> baseTypes(final ModelContext modelContext) {
    return new Predicate<ModelContext>() {
      @Override
      public boolean apply(ModelContext modelContext) {
        return isBaseType(ModelContext.fromParent(modelContext, modelContext.resolvedType(typeResolver)));
      }
    };
  }

  private boolean isBaseType(ModelContext modelContext) {
    String typeName = nameExtractor.typeName(modelContext);
    return Types.isBaseType(typeName);
  }

  private Predicate<ModelContext> ignorableTypes() {
    return new Predicate<ModelContext>() {
      @Override
      public boolean apply(ModelContext input) {
        return !input.getParent().hasSeenBefore(input.resolvedType(typeResolver));
      }
    };
  }

  private List<ModelContext> resolvedDependencies(ModelContext modelContext) {
    ResolvedType resolvedType = modelContext.alternateFor(modelContext.resolvedType(typeResolver));
    if (isBaseType(ModelContext.fromParent(modelContext, resolvedType))) {
      LOG.debug("Marking base type {} as seen", resolvedType.getSignature());
      modelContext.seen(resolvedType);
      return newArrayList();
    }
    List<ModelContext> dependencies = newArrayList(resolvedTypeParameters(modelContext, resolvedType));
    dependencies.addAll(resolvedArrayElementType(modelContext, resolvedType));
    dependencies.addAll(resolvedPropertiesAndFields(modelContext, resolvedType));
    return dependencies;
  }

  private List<ModelContext> resolvedArrayElementType(ModelContext modelContext, ResolvedType resolvedType) {
    List<ModelContext> parameters = newArrayList();
    if (resolvedType.isArray()) {
      ResolvedType elementType = resolvedType.getArrayElementType();
      LOG.debug("Adding type for element {}", elementType.getSignature());
      elementType = modelContext.alternateFor(elementType);
      ModelContext childContext = ModelContext.fromContainerParent(modelContext, elementType);
      parameters.add(childContext);
      LOG.debug("Recursively resolving dependencies for element {}", elementType.getSignature());
      parameters.addAll(resolvedDependencies(childContext));
    }
    return parameters;
  }

  private List<ModelContext> resolvedTypeParameters(ModelContext modelContext, ResolvedType resolvedType) {
    List<ModelContext> parameters = newArrayList();
    for (ResolvedType parameter : resolvedType.getTypeParameters()) {
      LOG.debug("Adding type for parameter {}", parameter.getSignature());
      parameter = modelContext.alternateFor(parameter);
      ModelContext childContext = (isContainerType(resolvedType) || 
                                   isMapType(resolvedType))?
              ModelContext.fromContainerParent(modelContext, parameter):ModelContext.fromParent(modelContext, parameter);
      parameters.add(childContext);
      LOG.debug("Recursively resolving dependencies for parameter {}", parameter.getSignature());
      parameters.addAll(resolvedDependencies(childContext));
    }
    return parameters;
  }

  private List<ModelContext> resolvedPropertiesAndFields(ModelContext modelContext, ResolvedType resolvedType) {
    if (modelContext.hasSeenBefore(resolvedType) || resolvedType.getErasedType().isEnum()) {
      return newArrayList();
    }
    modelContext.seen(resolvedType);
    List<ModelContext> propertiesContexts = newArrayList();
    ImmutableMap<String, ModelProperty> propertiesIndex
        = uniqueIndex(propertiesFor(modelContext, resolvedType), byPropertyName());
    LOG.debug("Inferred {} properties. Properties found {}", propertiesIndex.size(),
        Joiner.on(", ").join(propertiesIndex.keySet()));
    Map<String, ModelProperty> properties = newTreeMap();
    properties.putAll(propertiesIndex);
    modelContext.getBuilder().properties(properties);
    for (ModelProperty property : from(propertiesIndex.values())
            .filter(not(baseProperty(modelContext)))) {
      LOG.debug("Adding type {} for parameter {}", property.getType().getSignature(), property.getName());
      ModelContext childContext = ModelContext.fromParent(modelContext, property.getType());
      if (!isMapType(property.getType())) {
          property.updateModelRefFactory(modelRefFactory(childContext, nameExtractor));
          propertiesContexts.add(childContext);
      }
      propertiesContexts.addAll(maybeFromCollectionElementType(modelContext, property));
      propertiesContexts.addAll(maybeFromMapValueType(modelContext, property));
      propertiesContexts.addAll(maybeFromRegularType(childContext, property));
    }
    return propertiesContexts;
  }

  private Predicate<? super ModelProperty> baseProperty(final ModelContext modelContext) {
    return new Predicate<ModelProperty>() {
      @Override
      public boolean apply(ModelProperty input) {
        return isBaseType(ModelContext.fromParent(modelContext, input.getType()));
      }
    };
  }

  private List<ModelContext> maybeFromRegularType(ModelContext modelContext, ModelProperty property) {
    if (isContainerType(property.getType()) || isMapType(property.getType())) {
      return newArrayList();
    }
    LOG.debug("Recursively resolving dependencies for type {}", resolvedTypeSignature(property.getType()).or("<null>"));
    return newArrayList(resolvedDependencies(modelContext));
  }

  private List<ModelContext> maybeFromCollectionElementType(ModelContext modelContext, ModelProperty property) {
    List<ModelContext> dependencies = newArrayList();
    if (isContainerType(property.getType())) {
      ResolvedType collectionElementType = collectionElementType(property.getType());
      String resolvedTypeSignature = resolvedTypeSignature(collectionElementType).or("<null>");
      ModelContext childContext = ModelContext.fromParent(modelContext, collectionElementType);
      property.updateModelRefFactory(modelRefFactory(childContext, nameExtractor));
      if (!isBaseType(childContext)) {
        LOG.debug("Adding collectionElement type {}", resolvedTypeSignature);  
        dependencies.add(childContext);
      }
      LOG.debug("Recursively resolving dependencies for collectionElement type {}", resolvedTypeSignature);
      dependencies.addAll(resolvedDependencies(childContext));
    }
    return dependencies;
  }

  private List<ModelContext> maybeFromMapValueType(ModelContext modelContext, ModelProperty property) {
    List<ModelContext> dependencies = newArrayList();
    if (isMapType(property.getType())) {
      ResolvedType valueType = Maps.mapValueType(property.getType());
      String resolvedTypeSignature = resolvedTypeSignature(valueType).or("<null>");
      ModelContext childContext = ModelContext.fromParent(modelContext, valueType);
      property.updateModelRefFactory(modelRefFactory(childContext, nameExtractor));
      if (!isBaseType(childContext)) {
        LOG.debug("Adding value type {}", resolvedTypeSignature);
        dependencies.add(childContext);
      }
      LOG.debug("Recursively resolving dependencies for value type {}", resolvedTypeSignature);
      dependencies.addAll(resolvedDependencies(childContext));
    }
    return dependencies;
  }
  
  private Function<ModelProperty, String> byPropertyName() {
    return new Function<ModelProperty, String>() {
      @Override
      public String apply(ModelProperty input) {
        return input.getName();
      }
    };
  }
  
  private List<ModelProperty> propertiesFor(ModelContext modelContext, ResolvedType resolvedType) {
    return propertiesProvider.propertiesFor(resolvedType, modelContext);
  }


}
