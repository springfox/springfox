package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ModelProperty {
  @JsonProperty
  @JsonUnwrapped
  private final SwaggerDataType type;
  @JsonIgnore
  private final String qualifiedType;
  @JsonIgnore
  private final int position;
  private final Boolean required;
  private final String description;
  private final AllowableValues allowableValues;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private final ModelRef items;

  public ModelProperty(String type, String qualifiedType, int position, Boolean required, String description,
                       AllowableValues allowableValues, ModelRef items) {
    this.type = new DataType(type);
    this.qualifiedType = qualifiedType;
    this.position = position; //TODO Suspect unused
    this.required = required;
    this.description = description;
    this.allowableValues = allowableValues;
    this.items = items;
  }

  public SwaggerDataType getType() {
    return type;
  }

  public String getQualifiedType() {
    return qualifiedType;
  }

  public int getPosition() {
    return position;
  }

  public Boolean isRequired() {
    return required;
  }

  public String getDescription() {
    return description;
  }

  public AllowableValues getAllowableValues() {
    return allowableValues;
  }

  public ModelRef getItems() {
    return items;
  }
}
