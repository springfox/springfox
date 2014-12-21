package com.mangofactory.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.servicemodel.Model;
import com.mangofactory.servicemodel.ModelProperty;
import com.mangofactory.servicemodel.ModelRef;
import com.mangofactory.servicemodel.builder.ModelBuilder;
import com.mangofactory.servicemodel.builder.ModelPropertyBuilder;
import com.mangofactory.schema.property.provider.ModelPropertiesProvider;
import com.wordnik.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.schema.Collections.*;
import static com.mangofactory.schema.ResolvedTypes.*;


@Component
public class DefaultModelProvider implements ModelProvider {
  private final TypeResolver resolver;
  private final AlternateTypeProvider alternateTypeProvider;
  private final ModelPropertiesProvider propertiesProvider;
  private final ModelDependencyProvider dependencyProvider;

  @Autowired
  public DefaultModelProvider(TypeResolver resolver, AlternateTypeProvider alternateTypeProvider,
                              @Qualifier("default") ModelPropertiesProvider propertiesProvider,
                              ModelDependencyProvider dependencyProvider) {
    this.resolver = resolver;
    this.alternateTypeProvider = alternateTypeProvider;
    this.propertiesProvider = propertiesProvider;
    this.dependencyProvider = dependencyProvider;
  }

  @Override
  public com.google.common.base.Optional<Model> modelFor(ModelContext modelContext) {
    ResolvedType propertiesHost = alternateTypeProvider.alternateFor(modelContext.resolvedType(resolver));
    if (isContainerType(propertiesHost)
            || propertiesHost.getErasedType().isEnum()
            || Types.isBaseType(Types.typeNameFor(propertiesHost.getErasedType()))) {
      return Optional.absent();
    }
    Map<String, ModelProperty> properties = newLinkedHashMap();

    for (com.mangofactory.schema.property.ModelProperty each : properties(modelContext, propertiesHost)) {
      properties.put(each.getName(),
              new ModelPropertyBuilder()
                      .type(each.typeName(modelContext))
                      .qualifiedType(each.qualifiedTypeName())
                      .position(each.position())
                      .required(each.isRequired())
                      .description(each.propertyDescription())
                      .allowableValues(each.allowableValues())
                      .iItems(itemModelRef(each.getType()))
                      .build()
                    );
    }
    return Optional.of(
            new ModelBuilder()
                    .id(typeName(propertiesHost))
                    .name(typeName(propertiesHost))
                    .qualifiedType(simpleQualifiedTypeName(propertiesHost))
                    .properties(properties).description(modelDescription(propertiesHost))
                    .baseModel("")
                    .discriminator("")
                    .subTypes(new ArrayList<String>())
                    .build());
  }

  @Override
  public Map<String, Model> dependencies(ModelContext modelContext) {
    Map<String, Model> models = newHashMap();
    for (ResolvedType resolvedType : dependencyProvider.dependentModels(modelContext)) {
      Optional<Model> model = modelFor(ModelContext.fromParent(modelContext, resolvedType));
      if (model.isPresent()) {
        models.put(model.get().getName(), model.get());
      }
    }
    return models;
  }


  private String modelDescription(ResolvedType type) {
    ApiModel annotation = AnnotationUtils.findAnnotation(type.getErasedType(), ApiModel.class);
    if (annotation != null) {
      return annotation.description();
    }
    return "";
  }

  private Iterable<? extends com.mangofactory.schema.property.ModelProperty> properties(ModelContext context,
      ResolvedType propertiesHost) {

    if (context.isReturnType()) {
      return propertiesProvider.propertiesForSerialization(propertiesHost);
    } else {
      return propertiesProvider.propertiesForDeserialization(propertiesHost);
    }
  }


  private ModelRef itemModelRef(ResolvedType type) {
    if (!isContainerType(type)) {
      return null;
    }
    ResolvedType collectionElementType = collectionElementType(type);
    String elementTypeName = typeName(collectionElementType);
//    String qualifiedElementTypeName = simpleQualifiedTypeName(collectionElementType);

    return new ModelRef(elementTypeName);
//    if (!isBaseType(elementTypeName)) {
//
//
//      return new ModelRef(null, elementTypeName, qualifiedElementTypeName);
//    } else {
//      return new ModelRef(elementTypeName, "", qualifiedElementTypeName);
//    }
  }

}
