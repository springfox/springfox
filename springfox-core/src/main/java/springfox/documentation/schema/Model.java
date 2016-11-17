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
import com.google.common.base.Objects;

import java.util.List;
import java.util.Map;

public class Model {

  private final String id;
  private final String name;
  private final int index;
  private final ResolvedType type;
  private final String qualifiedType;
  private final Map<String, ModelProperty> properties;
  private final String description;
  private final String baseModel;
  private final String discriminator;
  private final List<String> subTypes;
  private final String example;

  public Model(
      String id,
      String name,
      int index,
      ResolvedType type,
      String qualifiedType,
      Map<String, ModelProperty> properties,
      String description,
      String baseModel,
      String discriminator,
      List<String> subTypes,
      String example) {

    this.id = id;
    this.name = name;
    this.index = index;
    this.type = type;
    this.qualifiedType = qualifiedType;
    this.properties = properties;
    this.description = description;
    this.baseModel = baseModel;
    this.discriminator = discriminator;
    this.subTypes = subTypes;
    this.example = example;
  }

  public String getId() {
    return (index > 0)?id + '_' + index:id;
  }

  public String getName() {
    return (index > 0)?name + '_' + index:name;
  }
  
  public int getIndex() {
	    return index;
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

  public String getExample() {
    return example;
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(id, name, type, qualifiedType, properties, description, baseModel, discriminator, subTypes, example);
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Model that = (Model) o;

    return Objects.equal(id, that.id) &&
        Objects.equal(name, that.name) &&
        Objects.equal(type, that.type) &&
        Objects.equal(qualifiedType, that.qualifiedType) &&
        Objects.equal(properties, that.properties) &&
        Objects.equal(description, that.description) &&
        Objects.equal(baseModel, that.baseModel) &&
        Objects.equal(discriminator, that.discriminator) &&
        Objects.equal(subTypes, that.subTypes) &&
        Objects.equal(example, that.example);
  }
}
