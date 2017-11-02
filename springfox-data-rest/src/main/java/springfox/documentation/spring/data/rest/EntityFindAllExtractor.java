/*
 *
 *  Copyright 2017-2018 the original author or authors.
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

import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.data.rest.webmvc.RestMediaTypes.SPRING_DATA_COMPACT_JSON;
import static org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.actionName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;

class EntityFindAllExtractor implements EntityOperationsExtractor {
  @Override
  public List<RequestHandler> extract(EntityContext context) {
    final List<RequestHandler> handlers = new ArrayList<>();
    final PersistentEntity<?, ?> entity = context.entity();
    CrudMethods crudMethods = context.crudMethods();
    TypeResolver resolver = context.getTypeResolver();
    RepositoryMetadata repository = context.getRepositoryMetadata();
    if (crudMethods.hasFindAllMethod()) {
      HandlerMethod handler = new HandlerMethod(
          context.getRepositoryInstance(),
          crudMethods.getFindAllMethod());
      ActionSpecification spec = new ActionSpecification(
          actionName(entity, crudMethods.getFindAllMethod()),
          String.format("%s%s",
              context.basePath(),
              context.resourcePath()),
          Collections.singleton(RequestMethod.GET),
          new HashSet<>(Arrays.asList(
              APPLICATION_JSON,
              HAL_JSON ,
              SPRING_DATA_COMPACT_JSON,
              TEXT_URI_LIST)),
          new HashSet<MediaType>(),
          handler,
          findAllParameters(context.getConfiguration(), context.getTypeResolver()),
          resolver.resolve(Resources.class, repository.getReturnedDomainClass(handler.getMethod())));
      handlers.add(new SpringDataRestRequestHandler(context, spec));
    }
    return handlers;
  }

  private ArrayList<ResolvedMethodParameter> findAllParameters(
      RepositoryRestConfiguration configuration,
      TypeResolver resolver) {
    ArrayList<ResolvedMethodParameter> parameters = new ArrayList<ResolvedMethodParameter>();
    parameters.add(new ResolvedMethodParameter(
        0,
        configuration.getPageParamName(),
        new ArrayList<>(),
        resolver.resolve(String.class)));
    parameters.add(new ResolvedMethodParameter(
        1,
        configuration.getLimitParamName(),
        new ArrayList<>(),
        resolver.resolve(String.class)));
    parameters.add(new ResolvedMethodParameter(
        2,
        configuration.getSortParamName(),
        new ArrayList<>(),
        resolver.resolve(String.class)));
    return parameters;
  }
}
