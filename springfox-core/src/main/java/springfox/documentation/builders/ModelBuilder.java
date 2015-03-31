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

package springfox.documentation.builders;

import com.fasterxml.classmate.ResolvedType;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelProperty;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

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
    this.id = BuilderDefaults.defaultIfAbsent(id, this.id);
    return this;
  }

  public ModelBuilder name(String name) {
    this.name = BuilderDefaults.defaultIfAbsent(name, this.name);
    return this;
  }

  public ModelBuilder qualifiedType(String qualifiedType) {
    this.qualifiedType = BuilderDefaults.defaultIfAbsent(qualifiedType, this.qualifiedType);
    return this;
  }

  public ModelBuilder properties(Map<String, ModelProperty> properties) {
    if (properties != null) {
      this.properties.putAll(properties);
    }
    return this;
  }

  public ModelBuilder description(String description) {
    this.description = BuilderDefaults.defaultIfAbsent(description, this.description);
    return this;
  }

  public ModelBuilder baseModel(String baseModel) {
    this.baseModel = BuilderDefaults.defaultIfAbsent(baseModel, this.baseModel);
    return this;
  }

  public ModelBuilder discriminator(String discriminator) {
    this.discriminator = BuilderDefaults.defaultIfAbsent(discriminator, this.discriminator);
    return this;
  }

  public ModelBuilder subTypes(List<String> subTypes) {
    if (subTypes != null) {
      this.subTypes.addAll(subTypes);
    }
    return this;
  }

  public ModelBuilder type(ResolvedType modelType) {
    this.modelType = BuilderDefaults.defaultIfAbsent(modelType, this.modelType);
    return this;
  }

  public Model build() {
    return new Model(id, name, modelType, qualifiedType, properties, description, baseModel, discriminator, subTypes);
  }
}