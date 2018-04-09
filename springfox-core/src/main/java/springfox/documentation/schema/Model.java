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

package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;

import java.util.List;
import java.util.Map;

public class Model {

  private final String id;
  private final String name;
  private final ResolvedType type;
  private final String qualifiedType;
  private final Map<String, ModelProperty> properties;
  private final String description;
  private final String baseModel;
  private final String discriminator;
  private final List<String> subTypes;
  private final Object example;
  private final Xml xml;

  public Model(
      String id,
      String name,
      ResolvedType type,
      String qualifiedType,
      Map<String, ModelProperty> properties,
      String description,
      String baseModel,
      String discriminator,
      List<String> subTypes,
      Object example,
      Xml xml) {

    this.id = id;
    this.name = name;
    this.type = type;
    this.qualifiedType = qualifiedType;
    this.properties = properties;
    this.description = description;
    this.baseModel = baseModel;
    this.discriminator = discriminator;
    this.subTypes = subTypes;
    this.example = example;
    this.xml = xml;
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

  public ResolvedType getType() {
    return type;
  }

  public Object getExample() {
    return example;
  }

  public Xml getXml() {
    return xml;
  }
}
