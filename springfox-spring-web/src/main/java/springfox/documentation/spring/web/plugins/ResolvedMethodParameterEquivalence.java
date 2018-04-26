/*
 *
 *  Copyright 2017-2018 the original author or authors.
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

import com.google.common.base.Equivalence;
import springfox.documentation.service.ResolvedMethodParameter;

import java.util.Objects;

class ResolvedMethodParameterEquivalence extends Equivalence<ResolvedMethodParameter> {
  @Override
  protected boolean doEquivalent(ResolvedMethodParameter a, ResolvedMethodParameter b) {
    return Objects.equals(a.defaultName(), b.defaultName())
        && Objects.equals(a.getParameterIndex(), b.getParameterIndex())
        && Objects.equals(a.getParameterType(), b.getParameterType());
  }

  @Override
  protected int doHash(ResolvedMethodParameter self) {
    return Objects.hash(
        self.defaultName(),
        self.getParameterIndex(),
        self.getParameterType());
  }
}
