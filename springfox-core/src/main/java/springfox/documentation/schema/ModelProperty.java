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
import com.google.common.base.Function;
import springfox.documentation.service.AllowableValues;

public class ModelProperty {
  private final String name;
  private final ResolvedType type;
  private final String qualifiedType;
  private final int position;
  private final Boolean required;
  private final boolean isHidden;
  private final Boolean readOnly;
  private final String description;
  private final AllowableValues allowableValues;
  private ModelReference modelRef;
  private final String example;
  private final String pattern;

  public ModelProperty(
      String name,
      ResolvedType type,
      String qualifiedType,
      int position,
      Boolean required,
      Boolean isHidden,
      Boolean readOnly,
      String description,
      AllowableValues allowableValues,
      String example,
      String pattern) {

    this.name = name;
    this.type = type;
    this.qualifiedType = qualifiedType;
    this.position = position;
    this.required = required;
    this.isHidden = isHidden;
    this.readOnly = readOnly;
    this.description = description;
    this.allowableValues = allowableValues;
    this.example = example;
    this.pattern = pattern;
  }

  public String getName() {
    return name;
  }

  public ResolvedType getType() {
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

  public Boolean isReadOnly() {
    return readOnly;
  }

  public String getDescription() {
    return description;
  }

  public AllowableValues getAllowableValues() {
    return allowableValues;
  }

  public ModelReference getModelRef() {
    return modelRef;
  }

  public boolean isHidden() {
    return isHidden;
  }

  public ModelProperty updateModelRef(Function<ResolvedType, ? extends ModelReference> modelRefFactory) {
    modelRef = modelRefFactory.apply(type);
    return this;
  }

  public String getExample() {
    return example;
  }

  public String getPattern() {
    return pattern;
  }
  
  @Override
  public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((qualifiedType == null) ? 0 : qualifiedType.hashCode());
      result = prime * result + position;
      result = prime * result + ((required == null) ? 0 : required.hashCode());
      result = prime * result + (isHidden ? 1231 : 1237);
      result = prime * result + ((readOnly == null) ? 0 : readOnly.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((allowableValues == null) ? 0 : allowableValues.hashCode());
      result = prime * result + ((modelRef == null) ? 0 : modelRef.hashCode());
      result = prime * result + ((example == null) ? 0 : example.hashCode());
      result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
      
      ModelProperty other = (ModelProperty) obj;
      
      if (name == null) {
          if (other.name != null) {
              return false;
          }
      } else if (!name.equals(other.name)) {
          return false;
      }
      if (type == null) {
          if (other.type != null) {
              return false;
          }
      } else if (!type.equals(other.type)) {
          return false;
      }
      if (qualifiedType == null) {
          if (other.getQualifiedType() != null) {
              return false;
          }
      } else if (!qualifiedType.equals(other.getQualifiedType())) {
          return false;
      }
      if (position != other.getPosition()) {
          return false;
      }
      if (required == null) {
          if (other.isRequired() != null) {
              return false;
          }
      } else if (!required.equals(other.isRequired())) {
          return false;
      }
      if (isHidden != other.isHidden) {
          return false;
      }
      if (readOnly == null) {
          if (other.readOnly != null) {
              return false;
          }
      } else if (!readOnly.equals(other.readOnly)) {
          return false;
      }
      if (description == null) {
          if (other.description != null) {
              return false;
          }
      } else if (!description.equals(other.description)) {
          return false;
      }
      if (allowableValues == null) {
          if (other.getAllowableValues() != null) {
              return false;
          }
      } else if (!allowableValues.equals(other.getAllowableValues())) {
          return false;
      }
      if (modelRef == null) {
          if (other.getModelRef() != null) {
              return false;
          }
      } else if (!modelRef.equals(other.getModelRef())) {
          return false;
      }
      if (example == null) {
          if (other.example != null) {
              return false;
          }
      } else if (!example.equals(other.example)) {
          return false;
      }
      if (pattern == null) {
          if (other.getPattern() != null) {
              return false;
          }
      } else if (!pattern.equals(other.getPattern())) {
          return false;
      }
      return true;
  }
}
