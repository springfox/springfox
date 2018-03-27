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

package springfox.documentation.spi.schema;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import springfox.documentation.schema.AlternateTypeRule;

import java.util.List;

import static com.google.common.collect.Lists.*;


public class AlternateTypeProvider {
  private List<AlternateTypeRule> rules = newArrayList();

  public AlternateTypeProvider(List<AlternateTypeRule> alternateTypeRules) {
    rules.addAll(alternateTypeRules);
  }

  public ResolvedType alternateFor(ResolvedType type) {
    Optional<AlternateTypeRule> matchingRule = firstMatchedRule(type);
    if (matchingRule.isPresent()) {
      return matchingRule.get().alternateFor(type);
    }
    return type;
  }

  public ResolvedType propertiesTypeFor(ResolvedType type) {
    Optional<AlternateTypeRule> matchingRule = firstMatchedRule(type);
    if (matchingRule.isPresent()) {
      return matchingRule.get().typeForProperties();
    }
    return type;
  }

  private Optional<AlternateTypeRule> firstMatchedRule(final ResolvedType type) {
    return FluentIterable.from(rules).firstMatch(thatAppliesTo(type));
  }

  public void addRule(AlternateTypeRule rule) {
    rules.add(rule);
  }

  private Predicate<AlternateTypeRule> thatAppliesTo(final ResolvedType type) {
    return new Predicate<AlternateTypeRule>() {
      @Override
      public boolean apply(AlternateTypeRule input) {
        return input.appliesTo(type);
      }
    };
  }
}
