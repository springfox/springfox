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

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Lists;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static org.springframework.data.rest.webmvc.RestMediaTypes.*;
import static org.springframework.http.MediaType.*;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.*;

class EntityFindAllExtractor implements EntityOperationsExtractor {
  @Override
  public List<RequestHandler> extract(EntityContext context) {
    final List<RequestHandler> handlers = newArrayList();
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
          newHashSet(RequestMethod.GET),
          newHashSet(
              APPLICATION_JSON,
              HAL_JSON ,
              SPRING_DATA_COMPACT_JSON,
              TEXT_URI_LIST),
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
        Lists.<Annotation>newArrayList(),
        resolver.resolve(String.class)));
    parameters.add(new ResolvedMethodParameter(
        1,
        configuration.getLimitParamName(),
        Lists.<Annotation>newArrayList(),
        resolver.resolve(String.class)));
    parameters.add(new ResolvedMethodParameter(
        2,
        configuration.getSortParamName(),
        Lists.<Annotation>newArrayList(),
        resolver.resolve(String.class)));
    return parameters;
  }
}
