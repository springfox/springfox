package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.provider.ModelPropertiesProvider;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.ModelImpl;
import com.wordnik.swagger.models.properties.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.Collections.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;


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
  public Optional<Model> modelFor(ModelContext modelContext) {
    ResolvedType propertiesHost = alternateTypeProvider.alternateFor(modelContext.resolvedType(resolver));
    if (isContainerType(propertiesHost)
            || propertiesHost.getErasedType().isEnum()
            || Types.isBaseType(Types.typeNameFor(propertiesHost.getErasedType()))) {
      return Optional.absent();
    }
    Map<String, Property> properties = newLinkedHashMap();

    for (com.mangofactory.swagger.models.property.ModelProperty each : properties(modelContext, propertiesHost)) {
      properties.put(each.getName(), Properties.from(each));
    }
    Model model = new ModelImpl()
            .name(typeName(propertiesHost))
            .example("");
    applyDescription(propertiesHost, model);
    applyRequired(model, properties);
    addProperties(model, properties);

    return Optional.of(model);

//            typeName(propertiesHost),
//            typeName(propertiesHost),
//            simpleQualifiedTypeName(propertiesHost),
//            properties,
//            modelDescription(propertiesHost), Optional.of(""),
//            Optional.<String>absent(),
//            newArrayList()));
  }

  private void addProperties(Model model, Map<String, Property> properties) {
    model.setProperties(properties);
  }

  private void applyRequired(Model model, Map<String, Property> properties) {

  }

  private void applyDescription(ResolvedType propertiesHost, Model model) {
    Optional<String> description = modelDescription(propertiesHost);
    if (description.isPresent()) {
      model.setDescription(description.get());
    }
  }

  @Override
  public Map<String, Model> dependencies(ModelContext modelContext) {
    Map<String, Model> models = newHashMap();
    for (ResolvedType resolvedType : dependencyProvider.dependentModels(modelContext)) {
      Optional<Model> model = modelFor(ModelContext.fromParent(modelContext, resolvedType));
      if (model.isPresent()) {
        models.put(String.valueOf(model.get().getProperties().get("name")), model.get());
      }
    }
    return models;
  }


  private Optional<String> modelDescription(ResolvedType type) {
    ApiModel annotation = AnnotationUtils.findAnnotation(type.getErasedType(), ApiModel.class);
    if (annotation != null) {
      return Optional.of(annotation.description());
    }
    return Optional.of("");
  }

  private Iterable<? extends com.mangofactory.swagger.models.property.ModelProperty> properties(ModelContext context,
      ResolvedType propertiesHost) {
    if (context.isReturnType()) {
      return propertiesProvider.propertiesForSerialization(propertiesHost);
    } else {
      return propertiesProvider.propertiesForDeserialization(propertiesHost);
    }
  }

//
//  private Optional<RefModel> itemModelRef(ResolvedType type) {
//    if (!isContainerType(type)) {
//      return Optional.absent();
//    }
//    ResolvedType collectionElementType = collectionElementType(type);
//    String elementTypeName = typeName(collectionElementType);
//    String qualifiedElementTypeName = simpleQualifiedTypeName(collectionElementType);
//    if (!isBaseType(elementTypeName)) {
//      return Optional.of(new RefModel(null,
//              Optional.of(elementTypeName), Optional.of(qualifiedElementTypeName)));
//    }
//    return Optional.absent();
//  }
//
//
//  private String id(Type type) {
//    return asResolved(resolver, type).getErasedType().getSimpleName();
//  }
}
