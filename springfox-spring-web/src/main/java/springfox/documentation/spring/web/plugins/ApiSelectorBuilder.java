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

package springfox.documentation.spring.web.plugins;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.contexts.ApiSelector;

import static com.google.common.base.Predicates.*;

public class ApiSelectorBuilder {
  private final Docket parent;
  private Predicate<RequestHandler> requestHandlerSelector = ApiSelector.DEFAULT.getRequestHandlerSelector();
  private Predicate<String> pathSelector = ApiSelector.DEFAULT.getPathSelector();

  public ApiSelectorBuilder(Docket parent) {
    this.parent = parent;
  }

  public ApiSelectorBuilder apis(Predicate<RequestHandler> selector) {
    requestHandlerSelector = and(requestHandlerSelector, selector);
    return this;
  }

  public ApiSelectorBuilder paths(Predicate<String> selector) {
    pathSelector = and(pathSelector, selector);
    return this;
  }

  public Docket build() {
    return parent.selector(new ApiSelector(combine(requestHandlerSelector, pathSelector), pathSelector));
  }

  private Predicate<RequestHandler> combine(Predicate<RequestHandler> requestHandlerSelector,
      Predicate<String> pathSelector) {
    return and(requestHandlerSelector, transform(pathSelector));
  }

  private Predicate<RequestHandler> transform(final Predicate<String> pathSelector) {
    return new Predicate<RequestHandler>() {
      @Override
      public boolean apply(RequestHandler input) {
        return Iterables.any(input.getPatternsCondition().getPatterns(), pathSelector);
      }
    };
  }
}
