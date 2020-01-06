/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

package springfox.documentation.spring.wrapper;

import java.util.Set;

public class PatternsRequestConditionWrapper
    implements PatternsRequestCondition<org.springframework.web.servlet.mvc.condition.PatternsRequestCondition> {

  private org.springframework.web.servlet.mvc.condition.PatternsRequestCondition condition;

  public PatternsRequestConditionWrapper(
      org.springframework.web.servlet.mvc.condition.PatternsRequestCondition condition) {
    this.condition = condition;
  }

  @Override
  public PatternsRequestCondition combine(
      PatternsRequestCondition<org.springframework.web.servlet.mvc.condition.PatternsRequestCondition> other) {
    if (other instanceof PatternsRequestConditionWrapper) {
      return new PatternsRequestConditionWrapper(
          this.condition.combine(
              ((PatternsRequestConditionWrapper) other).condition));
    }
    return this;
  }

  @Override
  public Set<String> getPatterns() {
    return this.condition.getPatterns();
  }
}
