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

package springfox.documentation.spring.web.csrf;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * The configurer by whom a `HttpServletRequestBlocker` will be
 * added to the argument resolvers chain
 *
 * @author liuxy
 */
@Configuration
public class CsrfWebFluxConfigurer implements WebFluxConfigurer {

    @PostConstruct
    public void registerDefaultLoader() {
        if (ClassUtils.isFlux()) {
            CsrfTokenLoader.DefaultOne.set(CsrfTokenWebFluxLoader.defaultOne());
        }
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new HttpServletRequestBlocker());
    }

    /**
     * An argument resolver which just prevent `HttpServletRequest`
     * from being processed with error
     */
    private class HttpServletRequestBlocker implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return HttpServletRequest.class.isAssignableFrom(parameter.getParameterType());
        }

        @Override
        public Mono<Object> resolveArgument(MethodParameter parameter,
                                            BindingContext bindingContext,
                                            ServerWebExchange exchange) {
            return Mono.empty();
        }
    }
}
