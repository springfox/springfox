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
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.*;
import static springfox.documentation.schema.Collections.*;

public class ResolvedTypes {

  private ResolvedTypes() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("deprecation")
  public static String simpleQualifiedTypeName(ResolvedType type) {
    if (type instanceof ResolvedPrimitiveType) {
      Type primitiveType = type.getErasedType();
      return Types.typeNameFor(primitiveType);
    }
    if (type instanceof ResolvedArrayType) {
      return Types.typeNameFor(type.getArrayElementType().getErasedType());
    }

    return type.getErasedType().getName();
  }

  public static AllowableValues allowableValues(ResolvedType resolvedType) {
    if (isContainerType(resolvedType)) {
      List<ResolvedType> typeParameters = resolvedType.getTypeParameters();
      if (typeParameters != null && typeParameters.size() == 1) {
        return Enums.allowableValues(typeParameters.get(0).getErasedType());
      }
    }
    return Enums.allowableValues(resolvedType.getErasedType());
  }

  public static Optional<String> resolvedTypeSignature(ResolvedType resolvedType) {
    return ofNullable(resolvedType).map(ResolvedType::getSignature);
  }

  public static boolean isVoid(ResolvedType returnType) {
    if (returnType == null) {
      return false;
    }
    return Void.class.equals(returnType.getErasedType()) || Void.TYPE.equals(returnType.getErasedType());
  }

  @SuppressWarnings("deprecation")
  public static Function<ResolvedType, ModelReference> modelRefFactory(
      final ModelContext parentContext,
      final EnumTypeDeterminer enumTypeDeterminer,
      final TypeNameExtractor typeNameExtractor,
      final Map<String, String> knownNames) {

    return new ModelReferenceProvider(
        typeNameExtractor,
        enumTypeDeterminer,
        parentContext,
        knownNames);
  }

  @SuppressWarnings("deprecation")
  public static Function<ResolvedType, ModelReference> modelRefFactory(
      final ModelContext parentContext,
      final EnumTypeDeterminer enumTypeDeterminer,
      final TypeNameExtractor typeNameExtractor) {

    return new ModelReferenceProvider(typeNameExtractor,
                                      enumTypeDeterminer,
                                      parentContext,
                                      new HashMap<>());
  }

}
