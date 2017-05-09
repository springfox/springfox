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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.MethodResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

class EntityContext {
  private final RepositoryRestConfiguration configuration;
  private final RepositoryInformation repository;
  private final ResourceMetadata resource;
  private final TypeResolver typeResolver;
  private final ResourceMappings mappings;
  private final PersistentEntities entities;
  private URI basePath;

  public EntityContext(
      RepositoryRestConfiguration configuration,
      RepositoryInformation repository,
      ResourceMetadata resource,
      TypeResolver typeResolver,
      ResourceMappings mappings,
      PersistentEntities entities) {

    this.configuration = configuration;
    this.repository = repository;
    this.resource = resource;
    this.typeResolver = typeResolver;
    this.basePath = configuration.getBaseUri();
    this.mappings = mappings;
    this.entities = entities;
  }


  public List<RequestHandler> requestHandlers() {
    List<RequestHandler> handlers = newArrayList();
    CrudMethods crudMethods = repository.getCrudMethods();
    if (crudMethods.hasSaveMethod()) {
      HandlerMethod handler = new HandlerMethod(
          repository.getRepositoryInterface(),
          crudMethods.getSaveMethod());
      ActionSpecification spec = new ActionSpecification(
          String.format("%s%s/{id}", configuration.getBasePath(), resource.getPath()),
          newHashSet(RequestMethod.PUT),
          new HashSet<MediaType>(),
          new HashSet<MediaType>(),
          handler,
          newArrayList(
              new ResolvedMethodParameter(
                  0,
                  "id",
                  pathAnnotations(handler),
                  typeResolver.resolve(repository.getIdType())),
              new ResolvedMethodParameter(
                  0,
                  "body",
                  bodyAnnotations(handler),
                  typeResolver.resolve(repository.getDomainType()))),
          typeResolver.resolve(repository.getReturnedDomainClass(handler.getMethod())));
      handlers.add(new SpringDataRestRequestHandler(this, spec));
    }
    if (crudMethods.hasDelete()) {
      HandlerMethod handler = new HandlerMethod(
          repository.getRepositoryInterface(),
          crudMethods.getDeleteMethod());
      ActionSpecification spec = new ActionSpecification(
          String.format("%s%s/{id}", basePath, resource.getPath()),
          newHashSet(RequestMethod.DELETE),
          new HashSet<MediaType>(),
          new HashSet<MediaType>(),
          handler,
          newArrayList(new ResolvedMethodParameter(
              0,
              "id",
              pathAnnotations(handler),
              typeResolver.resolve(repository.getIdType()))),
          typeResolver.resolve(repository.getReturnedDomainClass(handler.getMethod())));
      handlers.add(new SpringDataRestRequestHandler(this, spec));
    }
    if (crudMethods.hasFindOneMethod()) {
      HandlerMethod handler = new HandlerMethod(
          repository.getRepositoryInterface(),
          crudMethods.getFindOneMethod());
      ActionSpecification spec = new ActionSpecification(
          String.format("%s%s/{id}", configuration.getBasePath(), resource.getPath()),
          newHashSet(RequestMethod.GET),
          new HashSet<MediaType>(),
          new HashSet<MediaType>(),
          handler,
          newArrayList(new ResolvedMethodParameter(
              0,
              "id",
              pathAnnotations(handler),
              typeResolver.resolve(repository.getIdType()))),
          typeResolver.resolve(repository.getReturnedDomainClass(handler.getMethod())));
      handlers.add(new SpringDataRestRequestHandler(this, spec));
    }
    if (crudMethods.hasFindAllMethod()) {
      HandlerMethod handler = new HandlerMethod(
          repository.getRepositoryInterface(),
          crudMethods.getFindAllMethod());
      ActionSpecification spec = new ActionSpecification(
          String.format("%s%s", configuration.getBasePath(), resource.getPath()),
          newHashSet(RequestMethod.GET),
          new HashSet<MediaType>(),
          new HashSet<MediaType>(),
          handler,
          findAllParameters(),
          typeResolver.resolve(repository.getReturnedDomainClass(handler.getMethod())));
      handlers.add(new SpringDataRestRequestHandler(this, spec));
    }
    HandlerMethodResolver methodResolver = new HandlerMethodResolver(typeResolver);
    SearchResourceMappings searchMappings = mappings.getSearchResourceMappings(repository.getDomainType());
    for (MethodResourceMapping mapping : searchMappings.getExportedMappings()) {
      HandlerMethod handler = new HandlerMethod(
          repository.getRepositoryInterface(),
          mapping.getMethod());
      ActionSpecification spec = new ActionSpecification(
          String.format("%s%s/search%s", configuration.getBasePath(), resource.getPath(), mapping.getPath()),
          newHashSet(RequestMethod.GET),
          new HashSet<MediaType>(),
          new HashSet<MediaType>(),
          handler,
          methodResolver.methodParameters(handler),
          methodResolver.methodReturnType(handler));
      handlers.add(new SpringDataRestRequestHandler(this, spec));
    }
    return handlers;
  }

  private List<Annotation> pathAnnotations(HandlerMethod handler) {
    List<Annotation> annotations = newArrayList(AnnotationUtils.getAnnotations(handler.getMethod()));
    annotations.add(SynthesizedAnnotations.pathVariable("id"));
    return annotations;
  }

  private List<Annotation> bodyAnnotations(HandlerMethod handler) {
    List<Annotation> annotations = newArrayList(AnnotationUtils.getAnnotations(handler.getMethod()));
    annotations.add(SynthesizedAnnotations.REQUEST_BODY_ANNOTATION);
    return annotations;
  }

  private ArrayList<ResolvedMethodParameter> findAllParameters() {
    ArrayList<ResolvedMethodParameter> parameters = new ArrayList<ResolvedMethodParameter>();
    parameters.add(new ResolvedMethodParameter(
        0,
        configuration.getPageParamName(),
        Lists.<Annotation>newArrayList(),
        typeResolver.resolve(String.class)));
    parameters.add(new ResolvedMethodParameter(
        1,
        configuration.getLimitParamName(),
        Lists.<Annotation>newArrayList(),
        typeResolver.resolve(String.class)));
    parameters.add(new ResolvedMethodParameter(
        2,
        configuration.getSortParamName(),
        Lists.<Annotation>newArrayList(),
        typeResolver.resolve(String.class)));
    return parameters;
  }

  public String getName() {
    return resource.getDomainType().getSimpleName();
  }
}
