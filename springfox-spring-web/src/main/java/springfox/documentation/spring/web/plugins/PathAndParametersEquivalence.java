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

import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.util.Equivalence;
import springfox.documentation.util.Sets;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// RequestHandlerWrapper
class PathAndParametersEquivalence extends Equivalence<RequestHandler> {

  public PathAndParametersEquivalence(RequestHandler requestHandler) {
    super(requestHandler);
  }

  protected boolean doEquivalent(RequestHandler a, RequestHandler b) {
    return a.getPatternsCondition().equals(b.getPatternsCondition())
        && !Sets.intersection(a.supportedMethods(), b.supportedMethods()).isEmpty() && a.params().equals(b.params())
        && Sets.difference(wrapped(a.getParameters()), wrapped(b.getParameters())).isEmpty();
  }

  private Set<ResolvedMethodParameterWrapper> wrapped(List<ResolvedMethodParameter> parameters) {
    return parameters.stream().map(wrappingFunction()).collect(Collectors.toSet());
  }

  private Function<ResolvedMethodParameter, ResolvedMethodParameterWrapper> wrappingFunction() {
    return new Function<ResolvedMethodParameter, ResolvedMethodParameterWrapper>() {
      @Override
      public ResolvedMethodParameterWrapper apply(ResolvedMethodParameter input) {
        return new ResolvedMethodParameterWrapper(input);
      }
    };
  }

  protected int doHash(RequestHandler requestHandler) {
    return Objects.hash(requestHandler.getPatternsCondition().getPatterns(), requestHandler.supportedMethods(),
        requestHandler.params(), wrapped(requestHandler.getParameters()));
  }

  protected static class ResolvedMethodParameterWrapper extends Equivalence<ResolvedMethodParameter> {

    ResolvedMethodParameterWrapper(ResolvedMethodParameter delegate) {
      super(delegate);
    }

    protected boolean doEquivalent(ResolvedMethodParameter a, ResolvedMethodParameter b) {
      return Objects.equals(a.defaultName(), b.defaultName())
          && Objects.equals(a.getParameterIndex(), b.getParameterIndex())
          && Objects.equals(a.getParameterType(), b.getParameterType());
    }

    protected int doHash(ResolvedMethodParameter self) {
      return Objects.hash(self.defaultName(), self.getParameterIndex(), self.getParameterType());
    }
  }
}
