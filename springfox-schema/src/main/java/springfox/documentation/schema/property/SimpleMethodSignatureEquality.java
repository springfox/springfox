/*
 *
 *  Copyright 2016 the original author or authors.
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
package springfox.documentation.schema.property;

import java.lang.reflect.Method;
import java.util.function.BiPredicate;

class SimpleMethodSignatureEquality implements BiPredicate<Method, Method> {

  @Override
  public boolean test(Method first, Method other) {
    return first.getName().equals(other.getName())
        && first.getReturnType().equals(other.getReturnType())
        && equalParamTypes(first.getParameterTypes(), other.getParameterTypes());
  }

  private boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
    if (params1.length == params2.length) {
      for (int i = 0; i < params1.length; i++) {
        if (params1[i] != params2[i]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public int doHash(Method method) {
    return method.hashCode();
  }
}
