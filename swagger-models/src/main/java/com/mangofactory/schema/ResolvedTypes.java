package com.mangofactory.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedObjectType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import com.google.common.base.Optional;
import com.mangofactory.service.model.AllowableValues;

import java.lang.reflect.Type;
import java.util.List;

import static com.google.common.base.Preconditions.*;
import static com.mangofactory.schema.Collections.*;
import static com.mangofactory.schema.Types.*;

public class ResolvedTypes {
  private static GenericTypeNamingStrategy namingStrategy = new DefaultGenericTypeNamingStrategy();

  private ResolvedTypes() {
    throw new UnsupportedOperationException();
  }

  public static void setNamingStrategy(GenericTypeNamingStrategy strategy) {
    if (strategy != null) {
      namingStrategy = strategy;
    }
  }

  public static String typeName(ResolvedType type) {
    if (isContainerType(type)) {
      return containerType(type);
    }
    return innerTypeName(type);
  }

  //DK TODO: Eliminate this repetition
  public static String responseTypeName(ResolvedType type) {
    if (isContainerType(type)) {
      return String.format("%s%s", containerType(type), optionalContainerTypeQualifierForReturn(type));
    }
    return innerTypeName(type);
  }

  public static String parameterTypeName(ResolvedType type) {
    if (isContainerType(type)) {
      return String.format("%s%s", containerType(type), optionalContainerTypeQualifierForReturn(type));
    }
    return innerTypeName(type);
  }


  private static String optionalContainerTypeQualifierForReturn(ResolvedType type) {
    if (type.isArray()) {
      if ("object".equals(typeName(type.getArrayElementType()))) {
        return "";
      }
      return String.format("[%s]", typeName(type.getArrayElementType()));
    }

    List<ResolvedType> typeParameters = type.getTypeParameters();
    checkArgument(typeParameters.size() <= 1, "Expects container to have at most one generic parameter");
    if (typeParameters.size() == 0) {
      return "";
    }
    String qualifier = innerTypeName(typeParameters.get(0));
    if ("object".equals(qualifier)) {
      return "";
    }
    return String.format("[%s]", qualifier);
  }


  private static String innerTypeName(ResolvedType type) {
    if (type.getTypeParameters().size() > 0 && type.getErasedType().getTypeParameters().length > 0) {
      return genericTypeName(type);
    }
    return simpleTypeName(type);
  }

  public static String genericTypeName(ResolvedType resolvedType) {
    Class<?> erasedType = resolvedType.getErasedType();
    String simpleName = Optional
            .fromNullable(typeNameFor(erasedType))
            .or(erasedType.getSimpleName());
    StringBuilder sb = new StringBuilder(String.format("%s%s", simpleName, namingStrategy.getOpenGeneric()));
    boolean first = true;
    for (int index = 0; index < erasedType.getTypeParameters().length; index++) {
      ResolvedType typeParam = resolvedType.getTypeParameters().get(index);
      if (first) {
        sb.append(innerTypeName(typeParam));
        first = false;
      } else {
        sb.append(String.format("%s%s", namingStrategy.getTypeListDelimiter(),
                innerTypeName(typeParam)));
      }
    }
    sb.append(namingStrategy.getCloseGeneric());
    return sb.toString();
  }

  public static String simpleQualifiedTypeName(ResolvedType type) {
    if (type instanceof ResolvedPrimitiveType) {
      Type primitiveType = type.getErasedType();
      return typeNameFor(primitiveType);
    }
    if (type instanceof ResolvedArrayType) {
      return typeNameFor(type.getArrayElementType().getErasedType());
    }

    return type.getErasedType().getName();
  }


  public static String simpleTypeName(ResolvedType type) {
    Class<?> erasedType = type.getErasedType();
    if (type instanceof ResolvedPrimitiveType) {
      return typeNameFor(erasedType);
    } else if (erasedType.isEnum()) {
      return "string";
    } else if (type instanceof ResolvedArrayType) {
      return String.format("Array%s%s%s", namingStrategy.getOpenGeneric(),
              innerTypeName(type.getArrayElementType()), namingStrategy.getCloseGeneric());
    } else if (type instanceof ResolvedObjectType) {
      String typeName = typeNameFor(erasedType);
      if (typeName != null) {
        return typeName;
      }
    }
    return erasedType.getSimpleName();
  }


  public static ResolvedType asResolved(TypeResolver typeResolver, Type type) {
    if (type instanceof ResolvedType) {
      return (ResolvedType) type;
    }
    return typeResolver.resolve(type);
  }

  public static AllowableValues allowableValues(ResolvedType resolvedType) {
    return Enums.allowableValues(resolvedType.getErasedType());
  }
}
