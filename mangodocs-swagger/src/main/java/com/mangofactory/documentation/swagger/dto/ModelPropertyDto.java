package com.mangofactory.documentation.swagger.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class  ModelPropertyDto {
  @JsonProperty
  @JsonUnwrapped
  private SwaggerDataType type;
  @JsonIgnore
  private String qualifiedType;
  @JsonIgnore
  private int position;
  private Boolean required;
  private String description;
  @JsonProperty
  @JsonUnwrapped
  private AllowableValues allowableValues;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private DataType items;
  @JsonIgnore
  private String name;

  public ModelPropertyDto() {
  }

  public ModelPropertyDto(String name, String type, String qualifiedType, int position, Boolean required, String
          description, AllowableValues allowableValues, DataType items) {
    this.name = name;
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

  public DataType getItems() {
    return items;
  }

  public void setItems(DataType items) {
    this.items = items;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }
}
