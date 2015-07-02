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

package springfox.documentation.spring.web.scanners;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.PathProvider;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.service.ResourceGroupingStrategy;
import springfox.documentation.spi.service.contexts.ApiSelector;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Multimaps.*;

@Component
public class ApiListingReferenceScanner {

  private static final Logger LOG = LoggerFactory.getLogger(ApiListingReferenceScanner.class);

  public ApiListingReferenceScanResult scan(DocumentationContext context) {
    LOG.info("Scanning for api listing references");

    List<ApiListingReference> apiListingReferences = newArrayList();
    ArrayListMultimap<ResourceGroup, RequestMappingContext> resourceGroupRequestMappings
        = ArrayListMultimap.create();
    ApiSelector selector = context.getApiSelector();
    for (RequestMappingInfoHandlerMapping requestMappingHandlerMapping : context.getHandlerMappings()) {
      for (RequestHandler handler : matchingHandlers(requestMappingHandlerMapping, selector)) {
        RequestMappingInfo requestMappingInfo = handler.getRequestMapping();
        HandlerMethod handlerMethod = handler.getHandlerMethod();
        ResourceGroupingStrategy resourceGroupingStrategy = context.getResourceGroupingStrategy();
        Set<ResourceGroup> resourceGroups
            = resourceGroupingStrategy.getResourceGroups(requestMappingInfo, handlerMethod);
        String handlerMethodName = handlerMethod.getMethod().getName();

        RequestMappingContext requestMappingContext
            = new RequestMappingContext(context, requestMappingInfo, handlerMethod);

        LOG.info("Request mapping: {} belongs to groups: [{}] ", handlerMethodName, resourceGroups);
        for (ResourceGroup group : resourceGroups) {
          resourceGroupRequestMappings.put(group, requestMappingContext);
        }
      }
    }

    for (ResourceGroup resourceGroup : resourceGroupRequestMappings.keySet()) {
      String resourceGroupName = resourceGroup.getGroupName();
      String listingDescription = getResourceDescription(resourceGroupRequestMappings.get(resourceGroup), context);
      Integer position = resourceGroup.getPosition();
      PathProvider pathProvider = context.getPathProvider();
      String path = pathProvider.getResourceListingPath(context.getGroupName(), resourceGroupName);
      LOG.info("Created resource listing Path: {} Description: {} Position: {}",
          path, resourceGroupName, position);
      PathMappingAdjuster adjuster = new PathMappingAdjuster(context);
      apiListingReferences.add(new ApiListingReference(adjuster.adjustedPath(path), listingDescription, position));
    }
    List<ApiListingReference> sorted = context.getListingReferenceOrdering().sortedCopy(apiListingReferences);
    return new ApiListingReferenceScanResult(sorted, asMap(resourceGroupRequestMappings));
  }

  private String getResourceDescription(List<RequestMappingContext> requestMappings, DocumentationContext context) {
    Iterator<RequestMappingContext> iterator = requestMappings.iterator();
    if (!iterator.hasNext()) {
      return null;
    }

    RequestMappingContext requestMapping = iterator.next();
    ResourceGroupingStrategy resourceGroupingStrategy = context.getResourceGroupingStrategy();

    return resourceGroupingStrategy
        .getResourceDescription(requestMapping.getRequestMappingInfo(), requestMapping.getHandlerMethod());
  }

  private Set<RequestHandler> matchingHandlers(
      RequestMappingInfoHandlerMapping requestMappingHandlerMapping,
      ApiSelector selector) {
    return FluentIterable
        .from(requestMappingHandlerMapping.getHandlerMethods().entrySet())
        .transform(toRequestHandler())
        .filter(selector.getRequestHandlerSelector())
        .toSet();
  }

  private Function<Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
    return new Function<Entry<RequestMappingInfo, HandlerMethod>, RequestHandler>() {
      @Override
      public RequestHandler apply(Entry<RequestMappingInfo, HandlerMethod> input) {
        return new RequestHandler(input.getKey(), input.getValue());
      }
    };
  }
}
