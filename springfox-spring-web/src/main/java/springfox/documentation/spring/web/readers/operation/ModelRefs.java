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
package springfox.documentation.spring.web.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ResolvedTypes;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.schema.contexts.ModelContext;

import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.spi.schema.contexts.ModelContext.*;

public class ModelRefs {
  private ModelRefs() {
    throw new UnsupportedOperationException();
  }

  public static Optional<ModelRef> modelRef(Optional<ResolvedType> type,
        ModelContext modelContext,
        TypeNameExtractor nameExtractor) {
    if (!type.isPresent()) {
      return Optional.absent();
    }
    return Optional.of(modelRef(type.get(), modelContext, nameExtractor));
  }

  public static ModelRef modelRef(ResolvedType resolved,
       ModelContext modelContext,
       TypeNameExtractor nameExtractor) {

    if (isContainerType(resolved)) {
      ResolvedType collectionElementType = collectionElementType(resolved);
      String elementTypeName = nameExtractor.typeName(fromParent(modelContext, collectionElementType));
      AllowableValues allowableValues = ResolvedTypes.allowableValues(resolved);
      return new ModelRef(containerType(resolved), elementTypeName, allowableValues);
    }
    if (isMapType(resolved)) {
      String elementTypeName = nameExtractor.typeName(fromParent(modelContext, mapValueType(resolved)));
      return new ModelRef("Map", elementTypeName, true);
    }
    if (Void.class.equals(resolved.getErasedType()) || Void.TYPE.equals(resolved.getErasedType())) {
      return new ModelRef("void");
    }
    AllowableValues allowableValues = ResolvedTypes.allowableValues(resolved);
    String typeName = nameExtractor.typeName(fromParent(modelContext, resolved));
    return new ModelRef(typeName, null, allowableValues);
  }
}
