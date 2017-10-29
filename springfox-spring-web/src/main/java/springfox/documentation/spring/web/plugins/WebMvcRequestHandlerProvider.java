/*
 *
 *  Copyright 2015-2017 the original author or authors.
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

import static springfox.documentation.spi.service.contexts.Orderings.byPatternsCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebMvcRequestHandlerProvider implements RequestHandlerProvider {
  private final List<RequestMappingInfoHandlerMapping> handlerMappings;
  private final HandlerMethodResolver methodResolver;

  @Autowired
  public WebMvcRequestHandlerProvider(HandlerMethodResolver methodResolver,
      List<RequestMappingInfoHandlerMapping> handlerMappings) {
    this.handlerMappings = handlerMappings;
    this.methodResolver = methodResolver;
  }

  @Override
  public List<RequestHandler> requestHandlers() {
    if (handlerMappings == null) {
      return new ArrayList<>();
    }
    return handlerMappings.stream().flatMap(hm -> hm.getHandlerMethods().entrySet().stream()).map(toRequestHandler())
        .sorted(byPatternsCondition()).collect(Collectors.toList());
  }

  private Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
    return new Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler>() {
      @Override
      public WebMvcRequestHandler apply(Map.Entry<RequestMappingInfo, HandlerMethod> input) {
        return new WebMvcRequestHandler(methodResolver, input.getKey(), input.getValue());
      }
    };
  }
}
