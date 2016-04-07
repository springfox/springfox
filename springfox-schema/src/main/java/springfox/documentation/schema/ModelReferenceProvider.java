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
import com.google.common.base.Optional;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spi.schema.contexts.ModelContext;

import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.allowableValues;
import static springfox.documentation.spi.schema.contexts.ModelContext.fromParent;

class ModelReferenceProvider implements Function<ResolvedType, ModelReference> {
  private final TypeNameExtractor typeNameExtractor;
  private final ModelContext parentContext;

  public ModelReferenceProvider(TypeNameExtractor typeNameExtractor, ModelContext parentContext) {
    this.typeNameExtractor = typeNameExtractor;
    this.parentContext = parentContext;
  }

  @Override
  public ModelReference apply(ResolvedType type) {
    return collectionReference(type)
        .or(mapReference(type))
        .or(modelReference(type));
  }

  private ModelReference modelReference(ResolvedType type) {
    if (Void.class.equals(type.getErasedType()) || Void.TYPE.equals(type.getErasedType())) {
      return new ModelRef("void");
    }
    if (MultipartFile.class.isAssignableFrom(type.getErasedType())) {
      return new ModelRef("File");
    }
    String typeName = typeNameExtractor.typeName(fromParent(parentContext, type));
    return new ModelRef(typeName, allowableValues(type));
  }

  private Optional<ModelReference> mapReference(ResolvedType type) {
    if (isMapType(type)) {
      ResolvedType mapValueType = mapValueType(type);
      String typeName = typeNameExtractor.typeName(fromParent(parentContext, type));
      return Optional.<ModelReference>of(new ModelRef(typeName, apply(mapValueType), true));
    }
    return Optional.absent();
  }

  private Optional<ModelReference> collectionReference(ResolvedType type) {
    if (isContainerType(type)) {
      ResolvedType collectionElementType = collectionElementType(type);
      String typeName = typeNameExtractor.typeName(fromParent(parentContext, type));
      return Optional.<ModelReference>of(
          new ModelRef(
              typeName,
              apply(collectionElementType),
              allowableValues(collectionElementType)));
    }
    return Optional.absent();
  }
}
