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

import static springfox.documentation.spring.data.rest.RequestExtractionUtils.actionName;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.pathAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;

class EntityDeleteExtractor implements EntityOperationsExtractor {
  @Override
  public List<RequestHandler> extract(EntityContext context) {
    final List<RequestHandler> handlers = new ArrayList<>();
    final PersistentEntity<?, ?> entity = context.entity();
    CrudMethods crudMethods = context.crudMethods();
    TypeResolver resolver = context.getTypeResolver();
    RepositoryMetadata repository = context.getRepositoryMetadata();
    if (crudMethods.hasDelete()) {
      HandlerMethod handler = new HandlerMethod(
          context.getRepositoryInstance(),
          crudMethods.getDeleteMethod());
      ActionSpecification spec = new ActionSpecification(
          actionName(entity, crudMethods.getDeleteMethod()),
          String.format("%s%s/{id}",
              context.basePath(),
              context.resourcePath()),
          Collections.singleton(RequestMethod.DELETE),
          new HashSet<MediaType>(),
          new HashSet<MediaType>(),
          handler,
          Arrays.asList(new ResolvedMethodParameter(
              0,
              "id",
              pathAnnotations("id", handler),
              resolver.resolve(repository.getIdType()))),
          resolver.resolve(Void.TYPE));
      handlers.add(new SpringDataRestRequestHandler(context, spec));
    }
    return handlers;
  }
}
