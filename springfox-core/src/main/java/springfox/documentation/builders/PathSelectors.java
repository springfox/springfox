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

package springfox.documentation.builders;



import org.springframework.util.AntPathMatcher;

import java.util.function.Predicate;

public class PathSelectors {
  private PathSelectors() {
    throw new UnsupportedOperationException();
  }

  /**
   * Any path satisfies this condition
   *
   * @return predicate that is always true
   */
  public static Predicate<String> any() {
    return (each) -> true;
  }

  /**
   * No path satisfies this condition
   *
   * @return predicate that is always false
   */
  public static Predicate<String> none() {
    return (each) -> false;
  }

  /**
   * Predicate that evaluates the supplied regular expression
   *
   * @param pathRegex - regex
   * @return predicate that matches a particular regex
   */
  public static Predicate<String> regex(final String pathRegex) {
    return new Predicate<String>() {
      @Override
      public boolean test(String input) {
        return input.matches(pathRegex);
      }
    };
  }

  /**
   * Predicate that evaluates the supplied ant pattern
   *
   * @param antPattern - ant Pattern
   * @return predicate that matches a particular ant pattern
   */
  public static Predicate<String> ant(final String antPattern) {
    return new Predicate<String>() {
      @Override
      public boolean test(String input) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(antPattern, input);
      }
    };
  }
}
