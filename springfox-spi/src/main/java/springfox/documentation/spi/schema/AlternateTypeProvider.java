/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import springfox.documentation.schema.AlternateTypeRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public class AlternateTypeProvider {
  private List<AlternateTypeRule> rules = new ArrayList<>();

  public AlternateTypeProvider(List<AlternateTypeRule> alternateTypeRules) {
    rules.addAll(alternateTypeRules);
  }

  public ResolvedType alternateFor(ResolvedType type) {
    Optional<AlternateTypeRule> matchingRule = rules.stream()
            .filter(thatAppliesTo(type)).findFirst();
    if (matchingRule.isPresent()) {
      return matchingRule.get().alternateFor(type);
    }
    return type;
  }

  public void addRule(AlternateTypeRule rule) {
    rules.add(rule);
  }

  private Predicate<AlternateTypeRule> thatAppliesTo(final ResolvedType type) {
    return input -> input.appliesTo(type);
  }
}
