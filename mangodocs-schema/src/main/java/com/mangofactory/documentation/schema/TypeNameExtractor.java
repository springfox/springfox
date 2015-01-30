package com.mangofactory.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedObjectType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import com.google.common.collect.Iterables;
import com.mangofactory.documentation.schema.plugins.SchemaPluginsManager;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

import static com.google.common.base.Optional.*;
import static com.mangofactory.documentation.schema.Collections.*;
import static com.mangofactory.documentation.schema.Types.*;
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*;

@Component
public class TypeNameExtractor {
  private final TypeResolver typeResolver;
  private final GenericTypeNamingStrategy namingStrategy;
  private final SchemaPluginsManager pluginsManager;

  @Autowired
  public TypeNameExtractor(TypeResolver typeResolver,
                           GenericTypeNamingStrategy namingStrategy,
                           SchemaPluginsManager pluginsManager) {

    this.typeResolver = typeResolver;
    this.pluginsManager = pluginsManager;
    this.namingStrategy = fromNullable(namingStrategy).or(new DefaultGenericTypeNamingStrategy());
  }

  public String typeName(ModelContext context) {
    ResolvedType type = asResolved(context.getType());
    if (isContainerType(type)) {
      return String.format("%s%s", containerType(type), optionalContainerTypeQualifierForReturn(context));
    }
    return innerTypeName(type, context.getDocumentationType());
  }

  private String optionalContainerTypeQualifierForReturn(ModelContext context) {
    ResolvedType type = asResolved(context.getType());
    if (!context.shouldRenderContainerType()) {
      return "";
    }

    if (type.isArray()) {
      ModelContext childContext = fromParent(context, type.getArrayElementType());
      if ("object".equals(typeName(childContext))) {
        return "";
      }
      return String.format("[%s]", typeName(childContext));
    }

    List<ResolvedType> typeParameters = type.getTypeParameters();
    if (typeParameters.size() == 0) {
      return "";
    }
    String qualifier = innerTypeName(Iterables.getFirst(typeParameters, null), context.getDocumentationType());
    if ("object".equals(qualifier)) {
      return "";
    }
    return String.format("[%s]", qualifier);
  }

  private ResolvedType asResolved(Type type) {
    return typeResolver.resolve(type);
  }

  private String genericTypeName(ResolvedType resolvedType, DocumentationType documentationType) {
    Class<?> erasedType = resolvedType.getErasedType();
    String simpleName = fromNullable(typeNameFor(erasedType))
            .or(pluginsManager.typeName(new ModelNameContext(resolvedType.getErasedType(), documentationType)));
    StringBuilder sb = new StringBuilder(String.format("%s%s", simpleName, namingStrategy.getOpenGeneric()));
    boolean first = true;
    for (int index = 0; index < erasedType.getTypeParameters().length; index++) {
      ResolvedType typeParam = resolvedType.getTypeParameters().get(index);
      if (first) {
        sb.append(innerTypeName(typeParam, documentationType));
        first = false;
      } else {
        sb.append(String.format("%s%s", namingStrategy.getTypeListDelimiter(),
                innerTypeName(typeParam, documentationType)));
      }
    }
    sb.append(namingStrategy.getCloseGeneric());
    return sb.toString();
  }

  private String innerTypeName(ResolvedType type, DocumentationType documentationType) {
    if (type.getTypeParameters().size() > 0 && type.getErasedType().getTypeParameters().length > 0) {
      return genericTypeName(type, documentationType);
    }
    return simpleTypeName(type, documentationType);
  }

  private String simpleTypeName(ResolvedType type, DocumentationType documentationType) {
    Class<?> erasedType = type.getErasedType();
    if (type instanceof ResolvedPrimitiveType) {
      return typeNameFor(erasedType);
    } else if (erasedType.isEnum()) {
      return "string";
    } else if (type instanceof ResolvedArrayType) {
      return String.format("Array%s%s%s", namingStrategy.getOpenGeneric(),
              simpleTypeName(type.getArrayElementType(), documentationType), namingStrategy.getCloseGeneric());
    } else if (type instanceof ResolvedObjectType) {
      String typeName = typeNameFor(erasedType);
      if (typeName != null) {
        return typeName;
      }
    }
    return pluginsManager.typeName(new ModelNameContext(type.getErasedType(), documentationType));
  }
}
