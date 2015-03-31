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

public class AlternateTypeRule {
  private final ResolvedType original;
  private final ResolvedType alternate;

  /**
   * Instantiates a new Alternate type rule.
   *
   * @param original  the original type
   * @param alternate the alternate type
   */
  public AlternateTypeRule(ResolvedType original, ResolvedType alternate) {
    this.original = original;
    this.alternate = alternate;
  }

  /**
   * Provides alternate for supplier type.
   *
   * @param type the type
   * @return the alternate for the type
   */
  public ResolvedType alternateFor(ResolvedType type) {
    if (appliesTo(type)) {
      if (WildcardType.hasWildcards(original)) {
        return WildcardType.replaceWildcardsFrom(WildcardType.collectReplaceables(type, original), alternate);
      } else {
        return alternate;
      }
    }
    return original;
  }

  /**
   * Check if an alternate applies to type.
   *
   * @param type the source
   * @return the boolean
   */
  public boolean appliesTo(ResolvedType type) {
    return WildcardType.hasWildcards(original)
            && WildcardType.wildcardMatch(type, original)
            || WildcardType.exactMatch(original, type);
  }

}
