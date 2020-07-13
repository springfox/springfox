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
package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.spi.schema.contexts.ModelContext.*;

@Deprecated
class ModelReferenceProvider implements Function<ResolvedType, ModelReference> {
  private final TypeNameExtractor typeNameExtractor;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final ModelContext parentContext;
  private final Map<String, String> knownNames;

  ModelReferenceProvider(
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enumTypeDeterminer,
      ModelContext parentContext,
      Map<String, String> knownNames) {
    this.typeNameExtractor = typeNameExtractor;
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.parentContext = parentContext;
    this.knownNames = Collections.unmodifiableMap(knownNames);
  }

  @Override
  public ModelReference apply(ResolvedType type) {
    return collectionReference(type)
        .map(Optional::of)
        .orElse(mapReference(type))
        .orElse(modelReference(type));
  }

  private ModelReference modelReference(ResolvedType type) {
    if (Void.class.equals(type.getErasedType()) || Void.TYPE.equals(type.getErasedType())) {
      return new ModelRef("void");
    }
    if (MultipartFile.class.isAssignableFrom(type.getErasedType())) {
      return new ModelRef("__file");
    }
    if (FilePart.class.isAssignableFrom(type.getErasedType())) {
      return new ModelRef("__file");
    }
    String typeName = typeName(type);
    return new ModelRef(
        typeName,
        type.getBriefDescription(),
        null,
        allowableValues(type),
        modelId(fromParent(
            parentContext,
            type)));
  }

  private Optional<ModelReference> mapReference(ResolvedType type) {
    if (isMapType(type)) {
      ResolvedType mapValueType = mapValueType(type);
      return Optional.of(new ModelRef(
          typeName(type),
          apply(mapValueType),
          true));
    }
    return empty();
  }

  private Optional<ModelReference> collectionReference(ResolvedType type) {
    if (isContainerType(type)) {
      ResolvedType collectionElementType = collectionElementType(type);

      return Optional.of(new ModelRef(
          typeName(type),
          null,
          apply(collectionElementType),
          allowableValues(collectionElementType),
          null));
    }
    return empty();
  }

  private String typeName(ResolvedType type) {
    ModelContext context = fromParent(
        parentContext,
        type);
    return typeNameExtractor.typeName(
        context,
        knownNames);
  }

  private String modelId(ModelContext context) {
    ResolvedType type = context.getType();
    if (type instanceof ResolvedPrimitiveType
        || springfox.documentation.schema.Types.isBaseType(type)
        || isVoid(type)
        || enumTypeDeterminer.isEnum(type.getErasedType())) {
      return null;
    }
    return context.getModelId();
  }
}
