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
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import com.google.common.base.Function;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Type;

import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.spi.schema.contexts.ModelContext.*;

public class ResolvedTypes {

  private ResolvedTypes() {
    throw new UnsupportedOperationException();
  }

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
    return Enums.allowableValues(resolvedType.getErasedType());
  }

  public static Function<? super ResolvedType, ModelRef> modelRefFactory(final ModelContext parentContext,
                                                                         final TypeNameExtractor typeNameExtractor) {

    return new Function<ResolvedType, ModelRef>() {
      @Override
      public ModelRef apply(ResolvedType type) {
        if (isContainerType(type)) {
          ResolvedType collectionElementType = collectionElementType(type);
          String elementTypeName = typeNameExtractor.typeName(fromParent(parentContext, collectionElementType));
          return new ModelRef(Collections.containerType(type), elementTypeName);
        }
        if (isMapType(type)) {
          String elementTypeName = typeNameExtractor.typeName(fromParent(parentContext, springfox
              .documentation.schema.Maps.mapValueType(type)));
          return new ModelRef("Map", elementTypeName, true);
        }
        String typeName = typeNameExtractor.typeName(fromParent(parentContext, type));
        return new ModelRef(typeName);
      }
    };
  }
}
