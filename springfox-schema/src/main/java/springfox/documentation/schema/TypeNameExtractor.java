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
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedObjectType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Type;

import static com.google.common.base.Optional.*;
import static springfox.documentation.schema.Types.*;

@Component
public class TypeNameExtractor {
  private final TypeResolver typeResolver;
  private final SchemaPluginsManager pluginsManager;

  @Autowired
  public TypeNameExtractor(TypeResolver typeResolver, SchemaPluginsManager pluginsManager) {

    this.typeResolver = typeResolver;
    this.pluginsManager = pluginsManager;
  }

  public String typeName(ModelContext context) {
    ResolvedType type = asResolved(context.getType());
    if (Collections.isContainerType(type)) {
      return Collections.containerType(type);
    }
    return innerTypeName(type, context);
  }

  private ResolvedType asResolved(Type type) {
    return typeResolver.resolve(type);
  }

  private String genericTypeName(ResolvedType resolvedType, ModelContext context) {
    Class<?> erasedType = resolvedType.getErasedType();
    GenericTypeNamingStrategy namingStrategy = context.getGenericNamingStrategy();
    ModelNameContext nameContext = new ModelNameContext(resolvedType.getErasedType(), context.getDocumentationType());
    String simpleName = fromNullable(typeNameFor(erasedType)).or(pluginsManager.typeName(nameContext));
    StringBuilder sb = new StringBuilder(String.format("%s%s", simpleName, namingStrategy.getOpenGeneric()));
    boolean first = true;
    for (int index = 0; index < erasedType.getTypeParameters().length; index++) {
      ResolvedType typeParam = resolvedType.getTypeParameters().get(index);
      if (first) {
        sb.append(innerTypeName(typeParam, context));
        first = false;
      } else {
        sb.append(String.format("%s%s", namingStrategy.getTypeListDelimiter(),
                innerTypeName(typeParam, context)));
      }
    }
    sb.append(namingStrategy.getCloseGeneric());
    return sb.toString();
  }

  private String innerTypeName(ResolvedType type, ModelContext context) {
    if (type.getTypeParameters().size() > 0 && type.getErasedType().getTypeParameters().length > 0) {
      return genericTypeName(type, context);
    }
    return simpleTypeName(type, context);
  }

  private String simpleTypeName(ResolvedType type, ModelContext context) {
    Class<?> erasedType = type.getErasedType();
    if (type instanceof ResolvedPrimitiveType) {
      return typeNameFor(erasedType);
    } else if (erasedType.isEnum()) {
      return "string";
    } else if (type instanceof ResolvedArrayType) {
      GenericTypeNamingStrategy namingStrategy = context.getGenericNamingStrategy();
      return String.format("Array%s%s%s", namingStrategy.getOpenGeneric(),
              simpleTypeName(type.getArrayElementType(), context), namingStrategy.getCloseGeneric());
    } else if (type instanceof ResolvedObjectType) {
      String typeName = typeNameFor(erasedType);
      if (typeName != null) {
        return typeName;
      }
    }
    return pluginsManager.typeName(new ModelNameContext(type.getErasedType(), context.getDocumentationType()));
  }
}
