/**
 * Adds support for Spring Integration Http inbound adapters and gateways.
 * <p>
 * Builds upon the webmvc and webflux implementations, but uses Spring Integration http endpoint configurations to
 * create documentation.
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
 * <li>SpringIntegrationDocumentationPluginBootstrapper bootstraps on ContextRefreshedEvent because that is when IntegrationRequestMappingHandlerMapping detects handler methods - as opposed to DocumentationPluginBootstrapper, which uses SmartLifecycle</li>
 * <li>SpringIntegrationRequestHandlerProvider creates SpringIntegrationWebMvcRequestHandler for IntegrationRequestMappingHandlerMapping with Order LOWEST_PRECEDENCE and handles both webmvc and webflux HandlerMappings</li>
 * <li>SpringIntegrationWebMvcRequestHandler synthesizes RequestBody annotation from requestPayloadType of inbound endpoint</li>
 * </ul>
 * <p>
 * Changes in existing code:
 * <ul>
 * <li>{@code @Conditional(SpringIntegrationNotPresentInClassPathCondition.class)} on {@code DocumentationPluginsBootstrapper} to avoid bootstrapping the documentation plugins twice</li>
 * <li>Filter for Integration Handler Mappings in WebMvcRequestHandlerProvider</li>
 * </ul>
 *
 * @see springfox.documentation.spring.web.plugins.SpringIntegrationWebMvcRequestHandlerProvider
 * @see springfox.documentation.spring.web.SpringIntegrationWebMvcRequestHandler
 */
package springfox.documentation.spring.web;