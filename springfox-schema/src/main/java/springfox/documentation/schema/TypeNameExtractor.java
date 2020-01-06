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
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedObjectType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spi.schema.TypeNameProviderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Maps.*;
import static springfox.documentation.schema.Types.*;

@Component
public class TypeNameExtractor {
  private static final Logger LOG = LoggerFactory.getLogger(TypeNameExtractor.class);

  private final TypeResolver typeResolver;
  private final PluginRegistry<TypeNameProviderPlugin, DocumentationType> typeNameProviders;
  private final EnumTypeDeterminer enumTypeDeterminer;

  @Autowired
  public TypeNameExtractor(
      TypeResolver typeResolver,
      @Qualifier("typeNameProviderPluginRegistry")
          PluginRegistry<TypeNameProviderPlugin, DocumentationType> typeNameProviders,
      EnumTypeDeterminer enumTypeDeterminer) {

    this.typeResolver = typeResolver;
    this.typeNameProviders = typeNameProviders;
    this.enumTypeDeterminer = enumTypeDeterminer;
  }

  public String typeName(ModelContext context) {
    return typeName(
        context,
        new HashMap<>());
  }

  public String typeName(
      ModelContext context,
      Map<String, String> knownNames) {
    ResolvedType type = asResolved(context.getType());
    if (isContainerType(type)) {
      return containerType(type);
    }
    if (knownNames.containsKey(context.getTypeId())) {
      return knownNames.get(context.getTypeId());
    }
    return innerTypeName(
        type,
        context,
        knownNames);
  }

  private ResolvedType asResolved(Type type) {
    return typeResolver.resolve(type);
  }

  private String genericTypeName(
      ResolvedType resolvedType,
      ModelContext context,
      Map<String, String> knownNames) {
    Class<?> erasedType = resolvedType.getErasedType();
    GenericTypeNamingStrategy namingStrategy = context.getGenericNamingStrategy();
    String typeId = ModelContext.fromParent(
        context,
        resolvedType).getTypeId();
    if (knownNames.containsKey(typeId)) {
      return knownNames.get(typeId);
    }
    String simpleName = ofNullable(
        isContainerType(resolvedType) ? containerType(resolvedType) : typeNameFor(erasedType))
        .orElse(modelName(
            ModelContext.fromParent(
                context,
                resolvedType),
            knownNames));
    StringBuilder sb = new StringBuilder(String.format(
        "%s%s",
        simpleName,
        namingStrategy.getOpenGeneric()));
    boolean first = true;
    for (int index = 0; index < erasedType.getTypeParameters().length; index++) {
      ResolvedType typeParam = resolvedType.getTypeParameters().get(index);
      if (first) {
        sb.append(innerTypeName(
            typeParam,
            context,
            knownNames));
        first = false;
      } else {
        sb.append(String.format(
            "%s%s",
            namingStrategy.getTypeListDelimiter(),
            innerTypeName(
                typeParam,
                context,
                knownNames)));
      }
    }
    sb.append(namingStrategy.getCloseGeneric());
    return sb.toString();
  }

  private String innerTypeName(
      ResolvedType type,
      ModelContext context,
      Map<String, String> knownNames) {
    if (type.getTypeParameters().size() > 0 && type.getErasedType().getTypeParameters().length > 0) {
      return genericTypeName(
          type,
          context,
          knownNames);
    }
    return simpleTypeName(
        type,
        context,
        knownNames);
  }

  private String simpleTypeName(
      ResolvedType type,
      ModelContext context,
      Map<String, String> knownNames) {
    Class<?> erasedType = type.getErasedType();
    if (type instanceof ResolvedPrimitiveType) {
      return typeNameFor(erasedType);
    } else if (enumTypeDeterminer.isEnum(erasedType)) {
      return "string";
    } else if (type instanceof ResolvedArrayType) {
      GenericTypeNamingStrategy namingStrategy = context.getGenericNamingStrategy();
      return String.format(
          "Array%s%s%s",
          namingStrategy.getOpenGeneric(),
          simpleTypeName(
              type.getArrayElementType(),
              context,
              knownNames),
          namingStrategy.getCloseGeneric());
    } else if (type instanceof ResolvedObjectType) {
      String typeName = typeNameFor(erasedType);
      if (typeName != null) {
        return typeName;
      }
    }
    return modelName(
        ModelContext.fromParent(
            context,
            type),
        knownNames);
  }

  private String modelName(
      ModelContext context,
      Map<String, String> knownNames) {
    if (!isMapType(asResolved(context.getType())) && knownNames.containsKey(context.getTypeId())) {
      return knownNames.get(context.getTypeId());
    }
    TypeNameProviderPlugin selected = typeNameProviders.getPluginOrDefaultFor(
        context.getDocumentationType(),
        new DefaultTypeNameProvider());
    String modelName = selected.nameFor(((ResolvedType) context.getType()).getErasedType());
    LOG.debug(
        "Generated unique model named: {}, with model id: {}",
        modelName,
        context.getTypeId());
    return modelName;
  }
}
