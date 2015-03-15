package com.mangofactory.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

import java.util.List;
import java.util.Map;

public class Maps {
  private Maps() {
    throw new UnsupportedOperationException();
  }

  public static ResolvedType mapValueType(ResolvedType type) {
    if (Map.class.isAssignableFrom(type.getErasedType())) {
      return mapValueType(type, Map.class);
    } else {
      return new TypeResolver().resolve(Object.class);
    }
  }

  private static ResolvedType mapValueType(ResolvedType container, Class<Map> mapClass) {
    List<ResolvedType> resolvedTypes = container.typeParametersFor(mapClass);
    if (resolvedTypes.size() == 2) {
      return resolvedTypes.get(1);
    }
    return new TypeResolver().resolve(Object.class);
  }

  public static boolean isMapType(ResolvedType type) {
    return Map.class.isAssignableFrom(type.getErasedType());
  }
}
