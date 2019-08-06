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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.csrf.CsrfTokenLoader;

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
    @Autowired(required = false)
    private CsrfTokenLoader<?> loader;

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
     * This method depends indirectly on `springfox-spring-webmvc`
     * or `springfox-spring-webflux`, there should be one and only
     * one of them added as per your context.
     *
     * @param request be null in case of `springfox-spring-webflux`
     * @param exchange be null in case of `springfox-spring-webmvc`
     */
    @RequestMapping("csrf")
    @ResponseBody
    private ResponseEntity<?> csrf(HttpServletRequest request, ServerWebExchange exchange) {
        this.loader = loader == null ? CsrfTokenLoader.DefaultOne.get() : loader;
        if (loader == null) {
            throw new UnsupportedOperationException(
                    String.format("No %s could be found, please check your dependencies",
                            CsrfTokenLoader.class.getName()));
        }
        loader.wrap(request == null ? exchange : request);
        if (loader.isCorsRequest()) {
            return new ResponseEntity<>(loader.loadEmptiness(), HttpStatus.OK);
        }
        return new ResponseEntity<>(
                of(ofNullable(uiConfiguration).orElseGet(
                        UiConfigurationBuilder.builder()::build))
                        .map(UiConfiguration::getCsrfStrategy)
                        .map(csrfStrategy ->
                                (Object) csrfStrategy.loadCsrfToken(loader))
                        .orElse(loader.loadEmptiness()), HttpStatus.OK);
    }

}
