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

package springfox.documentation.spring.web;

import org.springframework.web.reactive.result.condition.PatternsRequestCondition;
import org.springframework.web.util.pattern.PathPattern;

import java.util.Set;
import java.util.stream.Collectors;

public class WebFluxPatternsRequestConditionWrapper
    implements springfox.documentation.spring.wrapper.PatternsRequestCondition<PatternsRequestCondition> {

  private final PatternsRequestCondition wrapped;

  public WebFluxPatternsRequestConditionWrapper(PatternsRequestCondition wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public springfox.documentation.spring.wrapper.PatternsRequestCondition combine(
      springfox.documentation.spring.wrapper.PatternsRequestCondition<PatternsRequestCondition> other) {
    if (other instanceof WebFluxPatternsRequestConditionWrapper && !this.equals(other)) {
      return new WebFluxPatternsRequestConditionWrapper(
          wrapped.combine(((WebFluxPatternsRequestConditionWrapper) other).wrapped));
    }
    return this;
  }

  @Override
  public Set<String> getPatterns() {
    return this.wrapped.getPatterns().stream()
        .map(PathPattern::getPatternString)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof WebFluxPatternsRequestConditionWrapper) {
      return wrapped.equals(((WebFluxPatternsRequestConditionWrapper) other).wrapped);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.wrapped.hashCode();
  }

  @Override
  public String toString() {
    return this.wrapped.toString();
  }
}

