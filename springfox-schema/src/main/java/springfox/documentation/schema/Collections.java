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
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Collections {
  private Collections() {
    throw new UnsupportedOperationException();
  }

  public static ResolvedType collectionElementType(ResolvedType type) {
    if (List.class.isAssignableFrom(type.getErasedType())) {
      return elementType(type, List.class);
    } else if (Set.class.isAssignableFrom(type.getErasedType())) {
      return elementType(type, Set.class);
    } else if (type.isArray()) {
      return type.getArrayElementType();
    } else if ((Collection.class.isAssignableFrom(type.getErasedType()) && !Maps.isMapType(type))) {
      return elementType(type, Collection.class);
    } else {
      return null;
    }
  }

  public static boolean isContainerType(@Nullable ResolvedType type) {
    if (type == null) {
      return false;
    }
    return List.class.isAssignableFrom(type.getErasedType()) ||
        Set.class.isAssignableFrom(type.getErasedType()) ||
        (Collection.class.isAssignableFrom(type.getErasedType()) && !Maps.isMapType(type)) ||
        type.isArray();
  }

  public static String containerType(ResolvedType type) {
    if (List.class.isAssignableFrom(type.getErasedType())) {
      return "List";
    } else if (Set.class.isAssignableFrom(type.getErasedType())) {
      return "Set";
    } else if (type.isArray()) {
      return "Array";
    } else if (Collection.class.isAssignableFrom(type.getErasedType()) && !Maps.isMapType(type)) {
      return "List";
    } else {
      throw new UnsupportedOperationException(String.format("Type is not collection type %s", type));
    }
  }

  public static CollectionType collectionType(ResolvedType type) {
    if (List.class.isAssignableFrom(type.getErasedType())) {
      return CollectionType.LIST;
    } else if (Set.class.isAssignableFrom(type.getErasedType())) {
      return CollectionType.SET;
    } else if (type.isArray()) {
      return CollectionType.ARRAY;
    } else if (Collection.class.isAssignableFrom(type.getErasedType()) && !Maps.isMapType(type)) {
      return CollectionType.LIST;
    } else {
      throw new UnsupportedOperationException(String.format("Type is not collection type %s", type));
    }
  }

  private static <T extends Collection> ResolvedType elementType(ResolvedType container, Class<T> collectionType) {
    List<ResolvedType> resolvedTypes = container.typeParametersFor(collectionType);
    if (resolvedTypes.size() == 1) {
      return resolvedTypes.get(0);
    }
    return new TypeResolver().resolve(Object.class);
  }
}
