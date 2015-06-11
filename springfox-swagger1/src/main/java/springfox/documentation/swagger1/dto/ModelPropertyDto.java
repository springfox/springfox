/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class  ModelPropertyDto {
  @JsonProperty
  @JsonUnwrapped
  private DataType type;
  @JsonIgnore
  private String qualifiedType;
  @JsonIgnore
  private int position;
  private Boolean required;
  private String description;
  @JsonProperty
  @JsonUnwrapped
  private AllowableValues allowableValues;

  @JsonIgnore
  private String name;

  public ModelPropertyDto() {
  }

  public ModelPropertyDto(String name, String type, String qualifiedType, int position, Boolean required, String
          description, AllowableValues allowableValues) {
    this.name = name;
    this.type = new DataType(type);
    this.qualifiedType = qualifiedType;
    this.position = position;
    this.required = required;
    this.description = description;
    this.allowableValues = allowableValues;
  }

  public DataType getType() {
    return type;
  }

  public void setType(DataType type) {
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
