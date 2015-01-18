package com.mangofactory.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.plugins.ModelContext;
import com.mangofactory.schema.property.provider.ModelPropertiesProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.schema.plugins.ModelContext.*;

@Component
public class ModelDependencyProvider {

  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;
  private final ModelPropertiesProvider propertiesProvider;
  private final TypeNameExtractor nameExtractor;

  @Autowired
  public ModelDependencyProvider(TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider,
                                 @Qualifier("default") ModelPropertiesProvider propertiesProvider,
                                 TypeNameExtractor nameExtractor) {
    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
    this.propertiesProvider = propertiesProvider;
    this.nameExtractor = nameExtractor;
  }

  public Set<ResolvedType> dependentModels(ModelContext modelContext) {
    return FluentIterable
            .from(resolvedDependencies(modelContext))
            .filter(ignorableTypes(modelContext))
            .filter(not(baseTypes(modelContext)))
            .toSet();
  }

  private Predicate<ResolvedType> baseTypes(final ModelContext modelContext) {
    return new Predicate<ResolvedType>() {
      @Override
      public boolean apply(ResolvedType resolvedType) {
        return isBaseType(fromParent(modelContext, resolvedType));
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
    ResolvedType resolvedType = alternateTypeProvider.alternateFor(modelContext.resolvedType(typeResolver));
    if (isBaseType(fromParent(modelContext, resolvedType))) {
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
      parameters.addAll(resolvedDependencies(fromParent(modelContext, parameter)));
    }
    return parameters;
  }

  private List<ResolvedType> resolvedPropertiesAndFields(ModelContext modelContext, ResolvedType resolvedType) {
    if (modelContext.hasSeenBefore(resolvedType) || resolvedType.getErasedType().isEnum()) {
      return newArrayList();
    }
    modelContext.seen(resolvedType);
    List<ResolvedType> properties = newArrayList();
    for (com.mangofactory.service.model.ModelProperty property : propertiesFor(modelContext, resolvedType)) {
      if (Types.typeNameFor(property.getType().getErasedType()) != null) {
        continue;
      }
      if (isBaseType(fromParent(modelContext, resolvedType))) {
        continue;
      }
      properties.add(property.getType());
      if (Collections.isContainerType(property.getType())) {
        ResolvedType collectionElementType = Collections.collectionElementType(property.getType());
        if (Types.typeNameFor(collectionElementType.getErasedType()) == null) {
          if (!isBaseType(fromParent(modelContext, collectionElementType))) {
            properties.add(collectionElementType);
          }
          properties.addAll(resolvedDependencies(fromParent(modelContext, collectionElementType)));
        }
        continue;
      }
      properties.addAll(resolvedDependencies(fromParent(modelContext, property.getType())));
    }
    return properties;
  }

  private List<com.mangofactory.service.model.ModelProperty> propertiesFor(ModelContext modelContext, ResolvedType resolvedType) {
    return propertiesProvider.propertiesFor(resolvedType, modelContext);
  }


}
