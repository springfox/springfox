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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ModelDto {

  @JsonIgnore
  private String id;
  @JsonProperty("id")
  private String name;
  @JsonIgnore
  private String qualifiedType;
  private TreeMap<String, ModelPropertyDto> properties = new TreeMap<String, ModelPropertyDto>();
  private String description;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String baseModel;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String discriminator;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<String> subTypes;

  public ModelDto() {
  }


  @SuppressWarnings("ParameterNumber")
  public ModelDto(
      String id,
      String name,
      String qualifiedType,
      Map<String, ModelPropertyDto> properties,
      String description,
      String baseModel,
      String discriminator,
      List<String> subTypes) {
    this.id = id;
    this.name = name;
    this.qualifiedType = qualifiedType;
    this.properties.putAll(properties);
    this.description = description;
    this.baseModel = baseModel;
    this.discriminator = discriminator;
    this.subTypes = subTypes;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getQualifiedType() {
    return qualifiedType;
  }

  public void setQualifiedType(String qualifiedType) {
    this.qualifiedType = qualifiedType;
  }

  public Map<String, ModelPropertyDto> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, ModelPropertyDto> properties) {
    this.properties.putAll(properties);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getBaseModel() {
    return baseModel;
  }

  public void setBaseModel(String baseModel) {
    this.baseModel = baseModel;
  }

  public String getDiscriminator() {
    return discriminator;
  }

  public void setDiscriminator(String discriminator) {
    this.discriminator = discriminator;
  }

  public List<String> getSubTypes() {
    return subTypes;
  }

  public void setSubTypes(List<String> subTypes) {
    this.subTypes = subTypes;
  }
}
