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
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;

import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.*;

class EntityRequestTemplate {
  private final ResourceMappings restMappings;
  private final Repositories repositories;
  private final RequestMappingInfo requestMappingInfo;
  private final HandlerMethod handlerMethod;
  private final TypeResolver typeResolver;

  EntityRequestTemplate(
      TypeResolver typeResolver,
      Repositories repositories,
      ResourceMappings restMappings,
      RequestMappingInfo requestMappingInfo,
      HandlerMethod handlerMethod) {
    this.typeResolver = typeResolver;
    this.repositories = repositories;
    this.restMappings = restMappings;
    this.requestMappingInfo = requestMappingInfo;
    this.handlerMethod = handlerMethod;
  }

  List<RequestHandler> operations() {
    List<RequestHandler> requestHandlers = newArrayList();
    for (ResourceMetadata resource : restMappings) {

      Class<?> domainType = resource.getDomainType();
      RepositoryInformation entity
          = repositories.getRepositoryInformationFor(domainType);
      Class<? extends Serializable> idType = entity.getIdType();
      //TODO: cache the id/type combo

      requestHandlers.add(
          new EntityRequestHandler(
              typeResolver,
              resource,
              idType,
              domainType,
              requestMappingInfo,
              handlerMethod));

    }
    return requestHandlers;
  }


}
