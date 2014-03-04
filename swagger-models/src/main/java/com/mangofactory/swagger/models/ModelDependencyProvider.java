package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class ModelDependencyProvider {

    private final TypeResolver typeResolver;
    private final ModelPropertiesProvider propertiesProvider;

    public ModelDependencyProvider(TypeResolver typeResolver, ModelPropertiesProvider propertiesProvider) {
        this.typeResolver = typeResolver;
        this.propertiesProvider = propertiesProvider;
    }

    public Set<ResolvedType> dependentModels(ModelContext modelContext) {
        return FluentIterable
                .from(resolvedDependencies(modelContext))
                .filter(ignorableTypes(modelContext))
                .toSet();
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
        ResolvedType resolvedType = modelContext.resolvedType(typeResolver);
        if (Types.isBaseType(typeName(resolvedType))) {
            return newArrayList();
        }
        return resolvedPropertiesAndFields(modelContext, resolvedType);
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
            return propertiesProvider.propertiesForDeserialization(resolvedType);
        } else {
            return propertiesProvider.propertiesForSerialization(resolvedType);
        }
    }


}
