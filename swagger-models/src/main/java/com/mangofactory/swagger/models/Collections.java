package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Collections {
  public static <T extends Collection> ResolvedType elementType(ResolvedType container, Class<T> collectionType) {
    List<ResolvedType> resolvedTypes = container.typeParametersFor(collectionType);
    if (resolvedTypes.size() == 1) {
      return resolvedTypes.get(0);
    }
    return new TypeResolver().resolve(Object.class);
  }

  public static ResolvedType collectionElementType(ResolvedType type) {
    if (List.class.isAssignableFrom(type.getErasedType())) {
      return Collections.elementType(type, List.class);
    } else if (Set.class.isAssignableFrom(type.getErasedType())) {
      return Collections.elementType(type, Set.class);
    } else if (type.isArray()) {
      return type.getArrayElementType();
    } else {
      return null;
    }
  }

  public static boolean isContainerType(ResolvedType type) {
    if (List.class.isAssignableFrom(type.getErasedType()) ||
            Set.class.isAssignableFrom(type.getErasedType()) ||
            type.isArray()) {
      return true;
    }
    return false;
  }

  public static String containerType(ResolvedType type) {
    if (List.class.isAssignableFrom(type.getErasedType())) {
      return "List";
    } else if (Set.class.isAssignableFrom(type.getErasedType())) {
      return "Set";
    } else if (type.isArray()) {
      return "Array";
    } else {
      throw new UnsupportedOperationException(String.format("Type is not collection type %s", type));
    }
  }
}
