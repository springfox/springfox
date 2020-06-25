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
package springfox.documentation.spring.web;

import springfox.documentation.spi.service.contexts.RequestMappingContext;

import java.util.Objects;
import java.util.function.BiPredicate;

public class OperationCachingEquivalence implements BiPredicate<RequestMappingContext, RequestMappingContext> {
  @Override
  public boolean test(RequestMappingContext first, RequestMappingContext second) {
    if (bothAreNull(first, second)) {
      return true;
    }
    if (eitherOfThemIsNull(first, second)) {
      return false;
    }
    return Objects.equals(first.key(), second.key())
        && Objects.equals(first.getGenericsNamingStrategy(), second.getGenericsNamingStrategy());
  }

  private boolean eitherOfThemIsNull(RequestMappingContext first, RequestMappingContext second) {
    return first.key() == null || second.key() == null;
  }

  private boolean bothAreNull(RequestMappingContext first, RequestMappingContext second) {
    return first.key() == null && second.key() == null;
  }

  public OperationCachingEquivalence.Wrapper wrap(RequestMappingContext outerContext) {
    return new Wrapper(outerContext, this);
  }

  public int doHash(RequestMappingContext requestMappingContext) {
    return Objects.hash(
        requestMappingContext.key(),
        requestMappingContext.getRequestMappingPattern(),
        requestMappingContext.getGenericsNamingStrategy());
  }

  public static class Wrapper {
    private final RequestMappingContext requestMappingContext;
    private final OperationCachingEquivalence equivalence;

    public Wrapper(RequestMappingContext requestMappingContext, OperationCachingEquivalence equivalence) {
      this.requestMappingContext = requestMappingContext;
      this.equivalence = equivalence;
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
          && equivalence.test(requestMappingContext, wrapper.requestMappingContext);
    }

    @Override
    public int hashCode() {
      return equivalence.doHash(requestMappingContext);
    }

    public RequestMappingContext get() {
      return requestMappingContext;
    }
  }

}
