package com.mangofactory.documentation.builders;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.documentation.schema.Model;
import com.mangofactory.documentation.schema.ModelProperty;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class ModelBuilder {
  private String id;
  private String name;
  private String qualifiedType;
  private String description;
  private String baseModel;
  private String discriminator;
  private ResolvedType modelType;

  private Map<String, ModelProperty> properties = newHashMap();
  private List<String> subTypes = newArrayList();

  public ModelBuilder id(String id) {
    this.id = defaultIfAbsent(id, this.id);
    return this;
  }

  public ModelBuilder name(String name) {
    this.name = defaultIfAbsent(name, this.name);
    return this;
  }

  public ModelBuilder qualifiedType(String qualifiedType) {
    this.qualifiedType = defaultIfAbsent(qualifiedType, this.qualifiedType);
    return this;
  }

  public ModelBuilder properties(Map<String, ModelProperty> properties) {
    if (properties != null) {
      this.properties.putAll(properties);
    }
    return this;
  }

  public ModelBuilder description(String description) {
    this.description = defaultIfAbsent(description, this.description);
    return this;
  }

  public ModelBuilder baseModel(String baseModel) {
    this.baseModel = defaultIfAbsent(baseModel, this.baseModel);
    return this;
  }

  public ModelBuilder discriminator(String discriminator) {
    this.discriminator = defaultIfAbsent(discriminator, this.discriminator);
    return this;
  }

  public ModelBuilder subTypes(List<String> subTypes) {
    if (subTypes != null) {
      this.subTypes.addAll(subTypes);
    }
    return this;
  }

  public ModelBuilder type(ResolvedType modelType) {
    this.modelType = defaultIfAbsent(modelType, this.modelType);
    return this;
  }

  public Model build() {
    return new Model(id, name, modelType, qualifiedType, properties, description, baseModel, discriminator, subTypes);
  }
}