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

package springfox.documentation.swagger1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Qualifier;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.swagger1.dto.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapper
public class DataTypeMapper {
  @ResponseTypeName
  public String responseTypeName(ModelReference modelRef) {
    if (modelRef == null) {
      return null;
    }
    if (modelRef.isCollection()) {
      return "array";
    }
    return modelRef.getType();
  }

  @OperationType
  public DataType operationTypeFromModelRef(ModelReference modelRef) {
    if (modelRef !=null) {
      return new DataType(operationTypeName(modelRef));
    }
    return null;
  }

  @Type
  public DataType typeFromModelRef(ModelReference modelRef) {
    if (modelRef != null) {
      if (modelRef.isCollection()) {
        return new DataType(String.format("%s[%s]", modelRef.getType(), modelRef.getItemType()));
      }
      return new DataType(modelRef.getType());
    }
    return null;
  }

  private String operationTypeName(ModelReference modelRef) {
    if (modelRef == null) {
      return null;
    }
    if (modelRef.isCollection()) {
      return String.format("%s[%s]", modelRef.getType(), modelRef.getItemType());
    }
    return modelRef.getType();
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface OperationType {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface Type {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface ResponseTypeName {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface ItemType {
  }
}
