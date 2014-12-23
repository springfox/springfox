package com.mangofactory.swagger.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ModelPropertyDto {
  @JsonProperty
  @JsonUnwrapped
  private SwaggerDataType type;
  @JsonIgnore
  private String qualifiedType;
  @JsonIgnore
  private int position;
  private Boolean required;
  private String description;
  private AllowableValues allowableValues;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private ModelRef items;

  public ModelPropertyDto() {
  }

  public ModelPropertyDto(String type, String qualifiedType, int position, Boolean required, String description,
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

  public void setType(SwaggerDataType type) {
    this.type = type;
  }

  public String getQualifiedType() {
    return qualifiedType;
  }

  public void setQualifiedType(String qualifiedType) {
    this.qualifiedType = qualifiedType;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public Boolean isRequired() {
    return required;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AllowableValues getAllowableValues() {
    return allowableValues;
  }

  public void setAllowableValues(AllowableValues allowableValues) {
    this.allowableValues = allowableValues;
  }

  public ModelRef getItems() {
    return items;
  }

  public void setItems(ModelRef items) {
    this.items = items;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }
}
