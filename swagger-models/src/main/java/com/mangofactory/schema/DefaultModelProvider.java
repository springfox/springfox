package com.mangofactory.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.property.provider.ModelPropertiesProvider;
import com.mangofactory.service.model.Model;
import com.mangofactory.service.model.ModelProperty;
import com.mangofactory.service.model.builder.ModelBuilder;
import com.wordnik.swagger.annotations.ApiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.schema.Collections.*;
import static com.mangofactory.schema.ResolvedTypes.*;


@Component
public class DefaultModelProvider implements ModelProvider {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultModelProvider.class);
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
    Map<String, ModelProperty> properties = newTreeMap();
    properties.putAll(uniqueIndex(properties(modelContext, propertiesHost), byPropertyName()));

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

  private Function<ModelProperty, String> byPropertyName() {
    return new Function<ModelProperty, String>() {
      @Override
      public String apply(ModelProperty input) {
        return input.getName();
      }
    };
  }

  private String modelDescription(ResolvedType type) {
    ApiModel annotation = AnnotationUtils.findAnnotation(type.getErasedType(), ApiModel.class);
    if (annotation != null) {
      return annotation.description();
    }
    return "";
  }

  private List<ModelProperty> properties(ModelContext context, ResolvedType propertiesHost) {
    return propertiesProvider.propertiesFor(propertiesHost, context);
  }
}
