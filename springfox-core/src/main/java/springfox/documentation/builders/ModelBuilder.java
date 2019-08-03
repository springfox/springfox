/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.Xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.*;
import static springfox.documentation.builders.BuilderDefaults.*;

/**
 * @deprecated
 * @since 3.0.0 use {@link ModelSpecificationBuilder} instead
 */
@Deprecated
public class ModelBuilder {
  private final String id;
  private String name;
  private String qualifiedType;
  private String description;
  private String baseModel;
  private String discriminator;
  private ResolvedType modelType;
  private Object example;
  private Xml xml;

  private Map<String, ModelProperty> properties = new HashMap<>();
  private List<ModelReference> subTypes = new ArrayList<>();

  /**
   * Constructor with the model
   *
   * @param model - existing model
   */
  public ModelBuilder(Model model) {
    this.id = model.getId();
    this.name = model.getName();
    this.qualifiedType = model.getQualifiedType();
    this.description = model.getDescription();
    this.baseModel = model.getBaseModel();
    this.discriminator = model.getDiscriminator();
    this.modelType = model.getType();
    this.example = model.getExample();
    this.xml = model.getXml();
    this.properties.putAll(model.getProperties());
    this.subTypes.addAll(model.getSubTypes());
  }

  /**
   * Constructor with the Id of the model
   *
   * @param id - identifier for the model
   */
  public ModelBuilder(String id) {
    this.id = id;
  }

  /**
   * Updates the Name of the model
   *
   * @param name - name of the model
   * @return this
   */
  public ModelBuilder name(String name) {
    this.name = defaultIfAbsent(
        name,
        this.name);
    return this;
  }

  /**
   * Fully package qualified name of the model
   *
   * @param qualifiedType - package qualified name
   * @return this
   */
  public ModelBuilder qualifiedType(String qualifiedType) {
    this.qualifiedType = defaultIfAbsent(
        qualifiedType,
        this.qualifiedType);
    return this;
  }

  /**
   * Updates the model properties
   *
   * @param properties - map of properties by name
   * @return this
   */
  public ModelBuilder properties(Map<String, ModelProperty> properties) {
    if (properties != null) {
      this.properties = new HashMap<>(properties);
    }
    return this;
  }

  /**
   * Update the description of the model
   *
   * @param description - description
   * @return this
   */
  public ModelBuilder description(String description) {
    this.description = defaultIfAbsent(
        description,
        this.description);
    return this;
  }

  /**
   * Update the based model
   *
   * @param baseModel - based model as in inherited parent model. We currently don't implement this feature
   * @return this
   */
  public ModelBuilder baseModel(String baseModel) {
    this.baseModel = defaultIfAbsent(
        baseModel,
        this.baseModel);
    return this;
  }

  /**
   * Updates inheritance discriminator, used to identify inherited subclasses. We currently don't implement this feature
   *
   * @param discriminator - inheritance discriminator
   * @return this
   */
  public ModelBuilder discriminator(String discriminator) {
    this.discriminator = defaultIfAbsent(
        discriminator,
        this.discriminator);
    return this;
  }

  /**
   * Updates the subclasses for this model.
   *
   * @param subTypes - Models inheriting from this model
   * @return this
   * @since 2.8.1 We changed the subType to be a model refers
   */
  public ModelBuilder subTypes(List<ModelReference> subTypes) {
    if (subTypes != null) {
      this.subTypes = new ArrayList<>(subTypes);
    }
    return this;
  }

  /**
   * Updates the Example for the model
   *
   * @param example - example of the model
   * @return this
   * @deprecated @since 2.8.1 Use the one which takes in an Object instead
   */
  @Deprecated
  public ModelBuilder example(String example) {
    this.example = defaultIfAbsent(
        example,
        this.example);
    return this;
  }

  /**
   * Updates the Example for the model
   *
   * @param example - example of the model
   * @return this
   * @since 2.8.1
   */
  public ModelBuilder example(Object example) {
    this.example = defaultIfAbsent(
        example,
        this.example);
    return this;
  }

  /**
   * Represents the type information with full fidelity of generics
   *
   * @param modelType - resolved type that represents the model
   * @return this
   */
  public ModelBuilder type(ResolvedType modelType) {
    this.modelType = defaultIfAbsent(
        modelType,
        this.modelType);
    return this;
  }

  public ModelBuilder xml(Xml xml) {
    this.xml = defaultIfAbsent(
        xml,
        this.xml);
    return this;
  }

  public Model build() {
    if (xml != null && isEmpty(xml.getName())) {
      xml.setName(name);
    }
    return new Model(
        id,
        name,
        modelType,
        qualifiedType,
        Collections.unmodifiableMap(properties),
        description,
        baseModel,
        discriminator,
        Collections.unmodifiableList(subTypes),
        example,
        xml);
  }
}
