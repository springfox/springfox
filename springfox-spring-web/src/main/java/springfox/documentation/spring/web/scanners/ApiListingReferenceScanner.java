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

package springfox.documentation.spring.web.scanners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.service.contexts.ApiSelector;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Component
public class ApiListingReferenceScanner {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiListingReferenceScanner.class);

  public ApiListingReferenceScanResult scan(DocumentationContext context) {
    LOGGER.debug("Scanning for api listing references");

    Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings
        = new HashMap<>();

    int requestMappingContextId = 0;

    ApiSelector selector = context.getApiSelector();
    Iterable<RequestHandler> matchingHandlers = context.getRequestHandlers().stream()
        .filter(selector.getRequestHandlerSelector()).collect(toList());
    for (RequestHandler handler : matchingHandlers) {
      ResourceGroup resourceGroup = new ResourceGroup(
          handler.groupName(),
          handler.declaringClass(),
          0);

      RequestMappingContext requestMappingContext
          = new RequestMappingContext(
          String.valueOf(requestMappingContextId),
          context,
          handler);

      resourceGroupRequestMappings.putIfAbsent(
          resourceGroup,
          new ArrayList<>());
      resourceGroupRequestMappings.get(resourceGroup).add(requestMappingContext);

      ++requestMappingContextId;
    }
    return new ApiListingReferenceScanResult(resourceGroupRequestMappings);
  }

}
