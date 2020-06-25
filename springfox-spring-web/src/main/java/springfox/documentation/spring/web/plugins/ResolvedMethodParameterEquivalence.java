/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.spring.web.plugins;

import springfox.documentation.service.ResolvedMethodParameter;

import java.util.Objects;
import java.util.function.BiPredicate;

class ResolvedMethodParameterEquivalence implements BiPredicate<ResolvedMethodParameter, ResolvedMethodParameter> {
  @Override
  public boolean test(ResolvedMethodParameter a, ResolvedMethodParameter b) {
    return Objects.equals(a.defaultName(), b.defaultName())
        && Objects.equals(a.getParameterIndex(), b.getParameterIndex())
        && Objects.equals(a.getParameterType(), b.getParameterType());
  }

  public int doHash(ResolvedMethodParameter self) {
    return Objects.hash(
        self.defaultName(),
        self.getParameterIndex(),
        self.getParameterType());
  }

  Wrapper wrap(ResolvedMethodParameter input) {
    return new Wrapper(input, this);
  }

  public class Wrapper {
    private final ResolvedMethodParameter parameter;
    private final ResolvedMethodParameterEquivalence equivalence;

    Wrapper(ResolvedMethodParameter parameter, ResolvedMethodParameterEquivalence equivalence) {
      this.parameter = parameter;
      this.equivalence = equivalence;
    }

    @Override
    public int hashCode() {
      return equivalence.doHash(parameter);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Wrapper wrapper = (Wrapper) o;
      return Objects.equals(equivalence, wrapper.equivalence)
          && equivalence.test(parameter, wrapper.parameter);
    }
  }
}
