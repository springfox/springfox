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
import springfox.documentation.util.Sets;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// RequestHandlerWrapper
class PathAndParametersEquivalence {

  private RequestHandler requestHandler;

  public PathAndParametersEquivalence(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  public RequestHandler get() {
    return requestHandler;
  }

  @Override
  public int hashCode() {
    if (requestHandler == null) {
      return 0;
    }
    return doHash(requestHandler);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PathAndParametersEquivalence) {
      obj = ((PathAndParametersEquivalence) obj).get();
    }
    if (requestHandler == obj) {
      return true;
    }
    if (requestHandler == null || obj == null) {
      return false;
    }
    if (!(obj instanceof RequestHandler)) {
      return false;
    }
    return doEquivalent(requestHandler, (RequestHandler) obj);
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

  protected static class ResolvedMethodParameterWrapper {

    private ResolvedMethodParameter delegate;

    ResolvedMethodParameterWrapper(ResolvedMethodParameter delegate) {
      this.delegate = delegate;
    }

    public ResolvedMethodParameter get() {
      return delegate;
    }

    @Override
    public int hashCode() {
      if (delegate == null) {
        return 0;
      }
      return doHash(delegate);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof ResolvedMethodParameterWrapper) {
        obj = ((ResolvedMethodParameterWrapper) obj).get();
      }
      if (delegate == obj) {
        return true;
      }
      if (delegate == null || obj == null) {
        return false;
      }
      if (delegate.getClass() != obj.getClass()) {
        return false;
      }
      return doEquivalent(delegate, (ResolvedMethodParameter) obj);
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
