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
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * The configurer by whom a `ServerWebExchangeBlocker` will be
 * added to the argument resolvers chain
 *
 * @author liuxy
 */
@Configuration
public class CsrfWebMvcConfigurer implements WebMvcConfigurer {

    @PostConstruct
    public void registerDefaultLoader() {
        if (ClassUtils.isMvc()) {
            CsrfTokenLoader.DefaultOne.set(CsrfTokenWebMvcLoader.defaultOne());
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ServerWebExchangeBlocker());
    }

    /**
     * An argument resolver which just prevent `ServerWebExchange`
     * from being processed with error
     */
    private class ServerWebExchangeBlocker implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return ServerWebExchange.class.isAssignableFrom(parameter.getParameterType());
        }

        @SuppressWarnings("RedundantThrows")
        @Override
        public Object resolveArgument(MethodParameter parameter,
                                      ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest,
                                      WebDataBinderFactory binderFactory) throws Exception {
            return null;
        }
    }
}
