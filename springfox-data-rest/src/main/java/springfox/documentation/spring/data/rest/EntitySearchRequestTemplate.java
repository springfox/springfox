/*
 *
 *  Copyright 2016 the original author or authors.
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
package springfox.documentation.spring.data.rest;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.data.rest.core.mapping.MethodResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.*;

class EntitySearchRequestTemplate {
  private final TypeResolver resolver;
  private final ResourceMappings mappings;
  private final RequestMappingInfo requestMapping;
  private final HandlerMethod handlerMethod;

  EntitySearchRequestTemplate(
      TypeResolver resolver,
      ResourceMappings mappings,
      RequestMappingInfo requestMapping,
      HandlerMethod handlerMethod) {

    this.resolver = resolver;
    this.mappings = mappings;
    this.requestMapping = requestMapping;
    this.handlerMethod = handlerMethod;
  }


  Collection<? extends RequestHandler> operations() {
    List<RequestHandler> requestHandlers = newArrayList();
    boolean collectionHandlerAdded = false;
    for (ResourceMetadata resource : mappings) {
      for (MethodResourceMapping searchResource : resource.getSearchResourceMappings()) {
        EntitySearchRequestHandler handler = new EntitySearchRequestHandler(
            resolver,
            requestMapping,
            new HandlerMethod(searchResource.getMethod().getClass(), searchResource.getMethod()),
            searchResource,
            resource);
        if (handler.resourceType() == ResourceType.ITEM || !collectionHandlerAdded) {
          requestHandlers.add(handler);
          if (!collectionHandlerAdded) {
            collectionHandlerAdded = (handler.resourceType() == ResourceType.COLLECTION);
          }
        }
      }
    }
    return requestHandlers;
  }

}
