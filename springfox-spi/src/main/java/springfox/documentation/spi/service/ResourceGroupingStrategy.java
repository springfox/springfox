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

package springfox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.core.service.ResourceGroup;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.core.spring.wrapper.RequestMappingInfo;

import java.util.Set;

/***
 * Deprecated in lieu of using ApiListingBuilderPlugins instead
 * @deprecated  @since 2.2.0 - only here for backward compatibility
 */
@Deprecated
public interface ResourceGroupingStrategy extends Plugin<DocumentationType> {
  /**
   * Gets the resource group for a particular request mapping.
   * Typically several requestMappings will live under a particular resource group.
   *
   * @param requestMappingInfo request mapping info
   * @param handlerMethod      handler method
   * @return Resource group uris.
   * @see <code>ApiListingReferenceScanner</code>
   */
  Set<ResourceGroup> getResourceGroups(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);


  /**
   * Gets the resource description.
   * This is typically the class name of the spring controller or value() attribute of any @Api annotations on that
   * controller class
   *
   * This method is deprecated since this functionality will likely be changed as we rework some of the internals
   * @deprecated @since 2.0.2
   * @param requestMappingInfo request mapping info
   * @param handlerMethod      handler method
   * @return description of the resource
   */
  @Deprecated
  String getResourceDescription(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);

  /**
   * Gets the position of the resource.
   *
   * This method is deprecated since this functionality will likely be changed as we rework some of the internals
   * @deprecated @since 2.0.2
   * @param requestMappingInfo mapping information
   * @param handlerMethod handler method
   * @return The numeric position
   */
  @Deprecated
  Integer getResourcePosition(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);

}
