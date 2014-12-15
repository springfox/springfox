package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

public class Model {

  private final String id;
  @JsonIgnore
  private final String name;
  @JsonIgnore
  private final String qualifiedType;
  private final Map<String, ModelProperty> properties;
  private final String description;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private final String baseModel;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private final String discriminator;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private final List<String> subTypes;

  public Model(String id, String name, String qualifiedType, Map<String, ModelProperty> properties, String
          description, String baseModel, String discriminator, List<String> subTypes) {
    this.id = id;
    this.name = name;
    this.qualifiedType = qualifiedType;
    this.properties = properties;
    this.description = description;
    this.baseModel = baseModel;
    this.discriminator = discriminator;
    this.subTypes = subTypes;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getQualifiedType() {
    return qualifiedType;
  }

  public Map<String, ModelProperty> getProperties() {
    return properties;
  }

  public String getDescription() {
    return description;
  }

  public String getBaseModel() {
    return baseModel;
  }

  public String getDiscriminator() {
    return discriminator;
  }

  public List<String> getSubTypes() {
    return subTypes;
  }
}
