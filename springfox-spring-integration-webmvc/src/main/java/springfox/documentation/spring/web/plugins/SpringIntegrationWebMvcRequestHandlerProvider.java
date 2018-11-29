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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.integration.http.inbound.IntegrationRequestMappingHandlerMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.SpringIntegrationWebMvcRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.spi.service.contexts.Orderings.*;
import static springfox.documentation.spring.web.paths.Paths.*;

@Component
@Order
public class SpringIntegrationWebMvcRequestHandlerProvider implements RequestHandlerProvider {
  private final List<IntegrationRequestMappingHandlerMapping> handlerMappings;
  private final HandlerMethodResolver methodResolver;
  private final String contextPath;
  private SpringIntegrationParametersProvider parametersProvider;

  @Autowired
  public SpringIntegrationWebMvcRequestHandlerProvider(
      Optional<ServletContext> servletContext,
      HandlerMethodResolver methodResolver,
      List<IntegrationRequestMappingHandlerMapping> handlerMappings,
      SpringIntegrationParametersProvider parametersProvider) {
    this.handlerMappings = handlerMappings;
    this.methodResolver = methodResolver;
    this.parametersProvider = parametersProvider;
    this.contextPath = servletContext
        .map(ServletContext::getContextPath)
        .orElse(ROOT);
  }

  @Override
  public List<RequestHandler> requestHandlers() {
    return nullToEmptyList(handlerMappings).stream()
        .map(toMappingEntries())
        .flatMap((Collection::stream))
        .map(toRequestHandler())
        .sorted(byPatternsCondition())
        .collect(toList());
  }

  private Function<IntegrationRequestMappingHandlerMapping,
      Set<Map.Entry<RequestMappingInfo, HandlerMethod>>> toMappingEntries() {
    return input -> {
      Map<RequestMappingInfo, HandlerMethod> handlerMethods = input.getHandlerMethods();
      return handlerMethods.entrySet();
    };
  }

  private Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
    return input -> new SpringIntegrationWebMvcRequestHandler(
        contextPath,
        methodResolver,
        input.getKey(),
        input.getValue(),
        parametersProvider);
  }
}
