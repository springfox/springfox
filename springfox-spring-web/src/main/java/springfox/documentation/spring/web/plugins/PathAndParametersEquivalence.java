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
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class PathAndParametersEquivalence extends Equivalence<RequestHandler> {
  private static final ResolvedMethodParameterEquivalence parameterEquivalence
      = new ResolvedMethodParameterEquivalence();

  @Override
  protected boolean doEquivalent(RequestHandler first, RequestHandler second) {

    return first.getPatternsCondition().equals(second.getPatternsCondition())
        && !Sets.intersection(first.supportedMethods(), second.supportedMethods()).isEmpty()
        && first.params().equals(second.params())
        && Sets.symmetricDifference(wrapped(first.getParameters()), wrapped(second.getParameters())).isEmpty();
  }

  private Set<Wrapper<ResolvedMethodParameter>> wrapped(List<ResolvedMethodParameter> parameters) {
    return parameters.stream()
        .map(parameterEquivalence::wrap)
        .collect(Collectors.toSet());
  }

  @Override
  protected int doHash(RequestHandler requestHandler) {
    return Objects.hashCode(
        requestHandler.getPatternsCondition().getPatterns(),
        requestHandler.supportedMethods(),
        requestHandler.params(),
        wrapped(requestHandler.getParameters()));
  }
}
