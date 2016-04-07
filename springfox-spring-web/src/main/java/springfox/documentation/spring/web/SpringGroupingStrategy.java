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

package springfox.documentation.spring.web;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ResourceGroupingStrategy;

import java.util.Set;

import static com.google.common.collect.Sets.*;
import static springfox.documentation.spring.web.paths.Paths.*;

/**
 * TODO - fix or remove
 * There are a lot of cases this strategy does not cover.
 * - request mappings of varying path depths,
 * - Paths beginning with path variables
 * - Controllers without top level request mappings
 */
public class SpringGroupingStrategy implements ResourceGroupingStrategy {
  @Override
  public Set<ResourceGroup> getResourceGroups(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
    return groups(handlerMethod);
  }

  @Override
  public String getResourceDescription(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
    return getDescription(handlerMethod.getBeanType());
  }

  @Override
  public Integer getResourcePosition(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
    return 0;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private Set<ResourceGroup> groups(HandlerMethod handlerMethod) {
    Class<?> controllerClazz = handlerMethod.getBeanType();
    String controllerAsGroup = splitCamelCase(controllerClazz.getSimpleName(), "-").toLowerCase();
    return newHashSet(new ResourceGroup(controllerAsGroup, controllerClazz));
  }

  private String getDescription(Class<?> controllerClass) {
    return splitCamelCase(controllerClass.getSimpleName(), " ");
  }
}
