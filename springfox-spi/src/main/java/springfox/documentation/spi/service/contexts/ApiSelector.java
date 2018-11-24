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

package springfox.documentation.spi.service.contexts;


import springfox.documentation.RequestHandler;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.PathSelectors;

import java.util.function.Predicate;

import static springfox.documentation.builders.RequestHandlerSelectors.*;

public class ApiSelector {
  public static final ApiSelector DEFAULT
      = new ApiSelector(
          (withClassAnnotation(ApiIgnore.class).negate()).and(
          (withMethodAnnotation(ApiIgnore.class).negate())), PathSelectors.any());
  private final Predicate<RequestHandler> requestHandlerSelector;
  private final Predicate<String> pathSelector;

  public ApiSelector(Predicate<RequestHandler> requestHandlerSelector, Predicate<String> pathSelector) {
    this.requestHandlerSelector = requestHandlerSelector;
    this.pathSelector = pathSelector;
  }

  public Predicate<RequestHandler> getRequestHandlerSelector() {
    return requestHandlerSelector;
  }

  public Predicate<String> getPathSelector() {
    return pathSelector;
  }
}
