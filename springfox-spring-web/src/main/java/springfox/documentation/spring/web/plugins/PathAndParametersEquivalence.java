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



import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

class PathAndParametersEquivalence extends Equivalence<RequestHandler> {
  private static final ResolvedMethodParameterEquivalence parameterEquivalence
      = new ResolvedMethodParameterEquivalence();

  @Override
  protected boolean doEquivalent(RequestHandler a, RequestHandler b) {

    return a.getPatternsCondition().equals(b.getPatternsCondition())
        && a.supportedMethods().stream().anyMatch(item -> b.supportedMethods().contains(item))
        && a.params().equals(b.params())
        && Objects.equals(wrapped(a.getParameters()), wrapped(b.getParameters()));
  }

  private Set<Wrapper<ResolvedMethodParameter>> wrapped(List<ResolvedMethodParameter> parameters) {
    return parameters.stream()
        .map(wrappingFunction())
        .collect(toSet());
  }

  private Function<ResolvedMethodParameter, Wrapper<ResolvedMethodParameter>> wrappingFunction() {
    return new Function<ResolvedMethodParameter, Wrapper<ResolvedMethodParameter>>() {
      @Override
      public Wrapper<ResolvedMethodParameter> apply(ResolvedMethodParameter input) {
        return parameterEquivalence.wrap(input);
      }
    };
  }

  @Override
  protected int doHash(RequestHandler requestHandler) {
    return Objects.hash(
        requestHandler.getPatternsCondition().getPatterns(),
        requestHandler.supportedMethods(),
        requestHandler.params(),
        wrapped(requestHandler.getParameters()));
  }
}
