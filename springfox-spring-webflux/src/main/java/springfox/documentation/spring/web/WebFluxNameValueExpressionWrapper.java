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

import springfox.documentation.spring.wrapper.NameValueExpression;

import java.util.HashSet;
import java.util.Set;

public class WebFluxNameValueExpressionWrapper<T> implements NameValueExpression {
    private org.springframework.web.reactive.result.condition.NameValueExpression<T> e;


  public static <T> Set<NameValueExpression<T>> from(
      Set<org.springframework.web.reactive.result.condition.NameValueExpression<T>> springSet) {

    return springSet.stream()
        .map(WebFluxNameValueExpressionWrapper::new)
        .collect(Collectors.toSet());
  }

    public WebFluxNameValueExpressionWrapper(org.springframework.web.reactive.result.condition.NameValueExpression<T> e) {
        this.e = e;
    }
  @Override
  public String getName() {
    return wrapped.getName();
  }


    @Override
    public Object getValue() {
        return this.e.getValue();
    }
  @Override
  public boolean isNegated() {
    return wrapped.isNegated();
  }


    @Override
    public int hashCode() {
        return this.e.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.e.equals(obj);
    }
}
