/*
 *
 *  Copyright 2016-2017 the original author or authors.
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

/**
 * Adds support for Spring Integration WebFlux inbound adapters and gateways.
 * <p>
 * Builds upon the springfox webflux implementation, but uses Spring Integration http
 * endpoint configurations to create documentation.
 *
 * <p>
 * Key extension points:
 * <ul>
 * <li>SpringIntegrationWebFluxRequestHandlerProvider creates SpringIntegrationWebFluxRequestHandler for
 * WebFluxIntegrationRequestMappingHandlerMapping with Order LOWEST_PRECEDENCE and handles webflux HandlerMappings</li>
 * <li>SpringIntegrationWebFluxRequestHandler synthesizes RequestBody annotation from
 * requestPayloadType field of inbound endpoint and RequestParam annotations from payloadExpression
 * and headerExpressions</li>
 * </ul>
 *
 * <p>
 * Changes to existing code:
 * <ul>
 * <li>Filter for Integration Handler Mappings in
 * {@link springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider}</li>
 * </ul>
 *
 * @see springfox.documentation.spring.web.plugins.SpringIntegrationWebFluxRequestHandlerProvider
 * @see springfox.documentation.spring.web.SpringIntegrationWebFluxRequestHandler
 */
package springfox.documentation.spring.web;