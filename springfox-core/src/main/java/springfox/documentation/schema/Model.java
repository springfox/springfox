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
  private final String example;

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
      String example) {

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

  public String getExample() {
    return example;
  }
  
  @Override
  public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((qualifiedType == null) ? 0 : qualifiedType.hashCode());
      result = prime * result + ((properties == null) ? 0 : properties.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((baseModel == null) ? 0 : baseModel.hashCode());   
      result = prime * result + ((discriminator == null) ? 0 : discriminator.hashCode());
      result = prime * result + ((subTypes == null) ? 0 : subTypes.hashCode());
      result = prime * result + ((example == null) ? 0 : example.hashCode());
      return result;
  }

  @Override
  public boolean equals(Object obj) {
      if (this == obj) {
          return true;
      }
      
      if (obj == null) {
          return false;
      }
      if (getClass() != obj.getClass()) {
          return false;
      }

      Model other = (Model) obj;
      
      if (id == null) {
          if (other.getId() != null) {
              return false;
          }
      } else if (!id.equals(other.getId())) {
          return false;
      }
      if (name == null) {
          if (other.getName() != null) {
              return false;
          }
      } else if (!name.equals(other.getName())) {
          return false;
      }

      if (type == null) {
          if (other.type != null) {
              return false;
          }
      } else if (!type.equals(other.getType())) {
          return false;
      }
      if (qualifiedType == null) {
          if (other.getQualifiedType() != null) {
              return false;
          }
      } else if (!qualifiedType.equals(other.getQualifiedType())) {
          return false;
      }
      if (properties == null) {
          if (other.properties != null) {
              return false;
          }
      } else if (!properties.equals(other.properties)) {
          return false;
      }  
      if (description == null) {
          if (other.description != null) {
              return false;
          }
      } else if (!description.equals(other.description)) {
          return false;
      }
      if (baseModel == null) {
          if (other.getBaseModel() != null) {
              return false;
          }
      } else if (!baseModel.equals(other.getBaseModel())) {
          return false;
      }
      if (discriminator == null) {
          if (other.discriminator != null) {
              return false;
          }
      } else if (!discriminator.equals(other.discriminator)) {
          return false;
      }
      if (example == null) {
          if (other.example != null) {
              return false;
          }
      } else if (!example.equals(other.example)) {
          return false;
      }
      if (subTypes == null) {
          if (other.getSubTypes() != null) {
              return false;
          }
      } else if (!subTypes.equals(other.getSubTypes())) {
          return false;
      }
      if (example == null) {
          if (other.getExample() != null) {
              return false;
          }
      } else if (!example.equals(other.getExample())) {
          return false;
      }
     
      return true;
  }
}
