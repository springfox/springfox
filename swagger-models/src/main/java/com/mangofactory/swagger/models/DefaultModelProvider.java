package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.provider.ModelPropertiesProvider;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.model.ModelProperty;
import com.wordnik.swagger.model.ModelRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import scala.Option;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.Collections.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static com.mangofactory.swagger.models.ScalaConverters.*;
import static com.mangofactory.swagger.models.Types.*;
import static scala.collection.JavaConversions.*;


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

    int index = 0;
    for (com.mangofactory.swagger.models.property.ModelProperty each : properties(modelContext, propertiesHost)) {
      properties.put(each.getName(), new ModelProperty(each.typeName(modelContext),
              each.qualifiedTypeName(),
              index,
              each.isRequired(),
              each.propertyDescription(),
              each.allowableValues(),
              itemModelRef(each.getType())
      ));
    }
    return Optional.of(new Model(typeName(propertiesHost),
            typeName(propertiesHost),
            simpleQualifiedTypeName(propertiesHost),
            toScalaLinkedHashMap(properties),
            modelDescription(propertiesHost), Option.apply(""),
            Option.<String>empty(),
            collectionAsScalaIterable(new ArrayList<String>()).toList()));
  }

  @Override
  public Map<String, Model> dependencies(ModelContext modelContext) {
    Map<String, Model> models = newHashMap();
    for (ResolvedType resolvedType : dependencyProvider.dependentModels(modelContext)) {
      Optional<Model> model = modelFor(ModelContext.fromParent(modelContext, resolvedType));
      if (model.isPresent()) {
        models.put(model.get().name(), model.get());
      }
    }
    return models;
  }


  private Option<String> modelDescription(ResolvedType type) {
    ApiModel annotation = AnnotationUtils.findAnnotation(type.getErasedType(), ApiModel.class);
    if (annotation != null) {
      return Option.apply(annotation.description());
    }
    return Option.apply("");
  }

  private Iterable<? extends com.mangofactory.swagger.models.property.ModelProperty> properties(ModelContext context,
      ResolvedType propertiesHost) {
    if (context.isReturnType()) {
      return propertiesProvider.propertiesForSerialization(propertiesHost);
    } else {
      return propertiesProvider.propertiesForDeserialization(propertiesHost);
    }
  }


  private Option<ModelRef> itemModelRef(ResolvedType type) {
    if (!isContainerType(type)) {
      return Option.empty();
    }
    ResolvedType collectionElementType = collectionElementType(type);
    String elementTypeName = typeName(collectionElementType);
    String qualifiedElementTypeName = simpleQualifiedTypeName(collectionElementType);
    if (!isBaseType(elementTypeName)) {
      return Option.apply(new ModelRef(null,
              Option.apply(elementTypeName), Option.apply(qualifiedElementTypeName)));
    } else {
      return Option.apply(new ModelRef(elementTypeName,
              Option.<String>empty(), Option.apply(qualifiedElementTypeName)));
    }
  }


  private String id(Type type) {
    return asResolved(resolver, type).getErasedType().getSimpleName();
  }
}
