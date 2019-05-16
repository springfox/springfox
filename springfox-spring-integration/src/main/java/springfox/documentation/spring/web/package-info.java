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
 * Adds support for Spring Integration Http inbound adapters and gateways.
 * <p>
 * Builds upon the springfox webmvc and webflux implementations, but uses Spring Integration http endpoint
 * configurations to create documentation. Spring Integration currently organizes the WebFlux/WebMvc split as follows.
 *
 * <ul>
 * <li>spring-integration-webflux pulls in spring-integration-http, but not spring-webmvc;
 * for spring-webflux it assumes that spring-boot-starter-webflux is present</li>
 * <li>spring-integration-http is necessary to use the Http endpoint, it does not pull in spring-webmvc;
 * for that it assumes that spring-boot-starter-web is present</li>
 * </ul>
 *
 * <p>
 * Key extension points:
 * <ul>
 * <li>SpringIntegrationDocumentationPluginBootstrapper bootstraps on ContextRefreshedEvent because that is when
 * IntegrationRequestMappingHandlerMapping detects handler methods - as opposed to DocumentationPluginBootstrapper,
 * which uses SmartLifecycle.</li>
 * <li>WebFlux or WebMvc specific implementations are necessary to provide request handlers</li>
 * </ul>
 * <p>
 * Changes in existing code:
 * <ul>
 * <li>{@code @Conditional(SpringIntegrationPluginNotPresentInClassPathCondition.class)} on
 * {@link springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper} to avoid bootstrapping
 * the documentation plugins twice</li>
 * <li>Filter for Integration Handler Mappings in WebMvcRequestHandlerProvider and WebFluxRequestHandlerProvider</li>
 * </ul>
 *
 * @see springfox.documentation.spring.web.plugins.SpringIntegrationDocumentationPluginsBootstrapper
 */
package springfox.documentation.spring.web;