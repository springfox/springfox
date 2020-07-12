/*
 *
 *  Copyright 2018 the original author or authors.
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
package springfox.petstore.webflux;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import springfox.documentation.schema.AlternateTypeRule;

import java.util.List;
import java.util.stream.Stream;

/**
 * Modified solution from https://github.com/springfox/springfox/issues/2287
 */
public class RecursiveAlternateTypeRule extends AlternateTypeRule {

  private final List<AlternateTypeRule> rules;

  public RecursiveAlternateTypeRule(
      TypeResolver typeResolver,
      List<AlternateTypeRule> rules) {
    // Unused but cannot be null
    super(typeResolver.resolve(Object.class), typeResolver.resolve(Object.class));
    this.rules = rules;
  }

  @Override
  public ResolvedType alternateFor(ResolvedType type) {
    Stream<ResolvedType> rStream = rules.stream().flatMap(rule -> Stream.of(rule.alternateFor(type)));
    ResolvedType newType = rStream.filter(alternateType -> alternateType != type)
        .findFirst()
        .orElse(type);

    if (appliesTo(newType)) {
      // Recursion happens here
      return alternateFor(newType);
    }

    return newType;
  }

  @Override
  public boolean appliesTo(ResolvedType type) {
    return rules.stream().anyMatch(rule -> rule.appliesTo(type));
  }
}