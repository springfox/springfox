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

import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import springfox.documentation.spi.service.contexts.RequestMappingContext;


public class OperationCachingEquivalence extends Equivalence<RequestMappingContext> {
  @Override
  protected boolean doEquivalent(RequestMappingContext first, RequestMappingContext second) {
    if (bothAreNull(first, second)) {
      return true;
    }
    if (eitherOfThemIsNull(first, second)) {
      return false;
    }
    return Objects.equal(first.key(), second.key())
        && Objects.equal(first.getGenericsNamingStrategy(), second.getGenericsNamingStrategy());
  }

  private boolean eitherOfThemIsNull(RequestMappingContext first, RequestMappingContext second) {
    return first.key() == null || second.key() == null;
  }

  private boolean bothAreNull(RequestMappingContext first, RequestMappingContext second) {
    return first.key() == null && second.key() == null;
  }

  @Override
  protected int doHash(RequestMappingContext requestMappingContext) {
    return Objects.hashCode(requestMappingContext.key(),
        requestMappingContext.getRequestMappingPattern(),
        requestMappingContext.getGenericsNamingStrategy());
  }
}
