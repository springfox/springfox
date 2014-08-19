package com.mangofactory.swagger.models;

import com.google.common.base.Optional;
import com.wordnik.swagger.converter.SwaggerSchemaConverter;
import com.wordnik.swagger.model.Model;
import scala.Option;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.ScalaConverters.*;

@Deprecated
public class SwaggerModelProvider implements ModelProvider {
  private SwaggerSchemaConverter parser = new SwaggerSchemaConverter();

  @Override
  public com.google.common.base.Optional<Model> modelFor(ModelContext modelContext) {
    checkArgument(modelContext.getType() instanceof Class, "Only classes are supported by native swagger " +
            "implmentation");
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    Option<Model> sModel = parser.read((Class) modelContext.getType(), new scala.collection.immutable.HashMap());
    return Optional.fromNullable(fromOption(sModel));
  }

  @Override
  public java.util.Map<String, Model> dependencies(ModelContext modelContext) {
    return newHashMap();
  }
}
