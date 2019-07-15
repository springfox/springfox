/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.swagger.common.ClassUtils;
import springfox.documentation.swagger.csrf.CsrfTokenLoader;
import springfox.documentation.swagger.csrf.CsrfTokenWebFluxLoader;
import springfox.documentation.swagger.csrf.CsrfTokenWebMvcLoader;
import springfox.documentation.swagger.csrf.MirrorCsrfToken;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Optional.*;

@Controller
@ApiIgnore
@RequestMapping("/swagger-resources")
public class ApiResourceController {


    @Autowired(required = false)
    private SecurityConfiguration securityConfiguration;
    @Autowired(required = false)
    private UiConfiguration uiConfiguration;

    private final SwaggerResourcesProvider swaggerResources;

    @Autowired
    public ApiResourceController(SwaggerResourcesProvider swaggerResources) {
        this.swaggerResources = swaggerResources;
    }

    @RequestMapping(value = "/configuration/security")
    @ResponseBody
    public ResponseEntity<SecurityConfiguration> securityConfiguration() {
        return new ResponseEntity<>(
                ofNullable(securityConfiguration).orElse(
                        SecurityConfigurationBuilder.builder().build()), HttpStatus.OK);
    }

    @RequestMapping(value = "/configuration/ui")
    @ResponseBody
    public ResponseEntity<UiConfiguration> uiConfiguration() {
        return new ResponseEntity<>(
                ofNullable(uiConfiguration).orElse(UiConfigurationBuilder.builder().build()), HttpStatus.OK);
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity<List<SwaggerResource>> swaggerResources() {
        return new ResponseEntity<>(swaggerResources.get(), HttpStatus.OK);
    }

    /**
     * Common behavior of loading csrf token
     *
     * @param loader the loader
     * @return the appropriate ResponseEntity
     */
    private <T> ResponseEntity<T> doLoadCsrfToken(CsrfTokenLoader<T> loader) {
        if (loader.isCorsRequest()) {
            return new ResponseEntity<>(loader.loadEmptiness(), HttpStatus.OK);
        }
        return new ResponseEntity<>(
                of(ofNullable(uiConfiguration).orElseGet(
                        UiConfigurationBuilder.builder()::build))
                        .map(UiConfiguration::getCsrfStrategy)
                        .map(csrfStrategy ->
                                csrfStrategy.loadCsrfToken(loader))
                        .orElse(loader.loadEmptiness()), HttpStatus.OK);
    }

    @RestController
    @ConditionalOnClass(name = ClassUtils.WEB_MVC_INDICATOR)
    public class CsrfWebMvcController {

        @RequestMapping("/swagger-resources/csrf")
        public ResponseEntity<MirrorCsrfToken> csrf(HttpServletRequest request) {
            return doLoadCsrfToken(CsrfTokenWebMvcLoader.wrap(request));
        }
    }

    @RestController
    @ConditionalOnClass(name = ClassUtils.WEB_FLUX_INDICATOR)
    public class CsrfWebFluxController {

        @RequestMapping("/swagger-resources/csrf")
        public ResponseEntity<Mono<MirrorCsrfToken>> csrf(ServerWebExchange exchange) {
            return doLoadCsrfToken(CsrfTokenWebFluxLoader.wrap(exchange));
        }
    }
}
