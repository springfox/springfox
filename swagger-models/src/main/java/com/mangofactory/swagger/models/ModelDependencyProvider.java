package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.provider.ModelPropertiesProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

@Component
public class ModelDependencyProvider {

  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;
  private final ModelPropertiesProvider propertiesProvider;

  @Autowired
  public ModelDependencyProvider(TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider,
                                 @Qualifier("default") ModelPropertiesProvider propertiesProvider) {
    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
    this.propertiesProvider = propertiesProvider;
  }

  public Set<ResolvedType> dependentModels(ModelContext modelContext) {
    return FluentIterable
            .from(resolvedDependencies(modelContext))
            .filter(ignorableTypes(modelContext))
            .filter(baseTypes())
            .toSet();
  }

  private Predicate<ResolvedType> baseTypes() {
    return new Predicate<ResolvedType>() {
      @Override
      public boolean apply(ResolvedType resolvedType) {
        return !Types.isBaseType(typeName(resolvedType));
      }
    };
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
    ResolvedType resolvedType = alternateTypeProvider.alternateFor(modelContext.resolvedType(typeResolver));
    if (Types.isBaseType(typeName(resolvedType))) {
      modelContext.seen(resolvedType);
      return newArrayList();
    }
    List<ResolvedType> dependencies = newArrayList(resolvedTypeParameters(modelContext, resolvedType));
    dependencies.addAll(resolvedPropertiesAndFields(modelContext, resolvedType));
    return dependencies;
  }

  private List<? extends ResolvedType> resolvedTypeParameters(ModelContext modelContext, ResolvedType resolvedType) {
    List<ResolvedType> parameters = newArrayList();
    for (ResolvedType parameter : resolvedType.getTypeParameters()) {
      parameters.add(alternateTypeProvider.alternateFor(parameter));
      parameters.addAll(resolvedDependencies(ModelContext.fromParent(modelContext, parameter)));
    }
    return parameters;
  }

  private List<ResolvedType> resolvedPropertiesAndFields(ModelContext modelContext, ResolvedType resolvedType) {
    if (modelContext.hasSeenBefore(resolvedType)) {
      return newArrayList();
    }
    modelContext.seen(resolvedType);
    List<ResolvedType> properties = newArrayList();
    for (ModelProperty property : propertiesFor(modelContext, resolvedType)) {
      if (Types.typeNameFor(property.getType().getErasedType()) != null) {
        continue;
      }
      if (Types.isBaseType(typeName(property.getType()))) {
        continue;
      }
      properties.add(property.getType());
      if (Collections.isContainerType(property.getType())) {
        ResolvedType collectionElementType = Collections.collectionElementType(property.getType());
        if (Types.typeNameFor(collectionElementType.getErasedType()) == null) {
          if (!Types.isBaseType(typeName(collectionElementType))) {
            properties.add(collectionElementType);
          }
          properties.addAll(resolvedDependencies(ModelContext.fromParent(modelContext, collectionElementType)));
        }
        continue;
      }
      properties.addAll(resolvedDependencies(ModelContext.fromParent(modelContext, property.getType())));
    }
    return properties;
  }

  private Iterable<? extends ModelProperty> propertiesFor(ModelContext modelContext, ResolvedType resolvedType) {
    if (modelContext.isReturnType()) {
      return propertiesProvider.propertiesForSerialization(resolvedType);
    } else {
      return propertiesProvider.propertiesForDeserialization(resolvedType);
    }
  }


}
