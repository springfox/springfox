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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.integration.http.inbound.IntegrationRequestMappingHandlerMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.SpringIntegrationRequestHandlerUtils;
import springfox.documentation.spring.web.SpringIntegrationWebMvcRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;
import static springfox.documentation.spi.service.contexts.Orderings.byPatternsCondition;
import static springfox.documentation.spring.web.paths.Paths.ROOT;

/**
 * TODO: check if the other classes which were built following the WebFlux example are necessary;
 *   maybe the WebFlux and WebMvc jars are sufficient.
 * TODO: concept to support explicit swagger annotations somehow - maybe a dummy method somewhere?
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE) // TODO: remove precedence?
public class SpringIntegrationRequestHandlerProvider implements RequestHandlerProvider {
    private final List<IntegrationRequestMappingHandlerMapping> handlerMappings;
    private final HandlerMethodResolver methodResolver;
    private final String contextPath;

    @Autowired
    public SpringIntegrationRequestHandlerProvider(
            Optional<ServletContext> servletContext,
            HandlerMethodResolver methodResolver,
            List<IntegrationRequestMappingHandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
        this.methodResolver = methodResolver;
        this.contextPath = servletContext
                .map(ServletContext::getContextPath)
                .orElse(ROOT);
    }

    @Override
    public List<RequestHandler> requestHandlers() {
        return nullToEmptyList(handlerMappings).stream()
                .map(toMappingEntries())
                .flatMap((entries -> StreamSupport.stream(entries.spliterator(), false)))
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
                new SpringIntegrationRequestHandlerUtils());
    }
}
