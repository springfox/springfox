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

import java.util.List;
import java.util.Map;

public class Maps {
  private Maps() {
    throw new UnsupportedOperationException();
  }

  public static ResolvedType mapValueType(ResolvedType type) {
    if (Map.class.isAssignableFrom(type.getErasedType())) {
      return mapValueType(
          type,
          Map.class);
    } else {
      return new TypeResolver().resolve(Object.class);
    }
  }

  private static ResolvedType mapValueType(
      ResolvedType container,
      Class<Map> mapClass) {
    List<ResolvedType> resolvedTypes = container.typeParametersFor(mapClass);
    if (resolvedTypes.size() == 2) {
      return resolvedTypes.get(1);
    }
    return new TypeResolver().resolve(Object.class);
  }

  public static ResolvedType mapKeyType(ResolvedType type) {
    if (Map.class.isAssignableFrom(type.getErasedType())) {
      return mapKeyType(
          type,
          Map.class);
    } else {
      return new TypeResolver().resolve(Object.class);
    }
  }

  private static ResolvedType mapKeyType(
      ResolvedType container,
      Class<Map> mapClass) {
    List<ResolvedType> resolvedTypes = container.typeParametersFor(mapClass);
    if (resolvedTypes.size() == 2) {
      return resolvedTypes.get(0);
    }
    return new TypeResolver().resolve(Object.class);
  }

  public static boolean isMapType(ResolvedType type) {
    return Map.class.isAssignableFrom(type.getErasedType());
  }
}
