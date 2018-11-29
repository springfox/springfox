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
 * Adds support for Spring Integration WebMvc inbound adapters and gateways.
 * <p>
 * Builds upon the springfox webmvc implementation, but uses Spring Integration http
 * endpoint configurations to create documentation.
 *
 * <p>
 * Key extension points:
 * <ul>
 * <li>SpringIntegrationWebMvcRequestHandlerProvider creates SpringIntegrationWebMvcRequestHandler for
 * WebMvcIntegrationRequestMappingHandlerMapping with Order LOWEST_PRECEDENCE and handles webmvc HandlerMappings</li>
 * <li>SpringIntegrationWebMvcRequestHandler synthesizes RequestBody annotation from
 * requestPayloadType field of inbound endpoint and RequestParam annotations from payloadExpression
 * and headerExpressions</li>
 * </ul>
 *
 * <p>
 * Changes to existing code:
 * <ul>
 * <li>Filter for Integration Handler Mappings in
 * {@link springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider}</li>
 * </ul>
 *
 * @see springfox.documentation.spring.web.plugins.SpringIntegrationWebMvcRequestHandlerProvider
 * @see springfox.documentation.spring.web.SpringIntegrationWebMvcRequestHandler
 */
package springfox.documentation.spring.web;