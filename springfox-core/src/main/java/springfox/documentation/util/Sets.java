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
package springfox.documentation.util;

import java.util.HashSet;
import java.util.Set;

public class Sets {

  public static <T> Set<T> difference(Set<T> a, Set<T> b) {
    HashSet<T> diff = new HashSet<>(a);
    diff.removeAll(b);
    return diff;
  }

  public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
    HashSet<T> intersection = new HashSet<>(a);
    intersection.retainAll(b);
    return intersection;
  }
}
