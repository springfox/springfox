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

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.MethodResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.data.rest.webmvc.mapping.Associations;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.schema.Collections;
import springfox.documentation.schema.Types;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static org.springframework.data.rest.webmvc.RestMediaTypes.*;

class EntityContext {
  private final RepositoryRestConfiguration configuration;
  private final RepositoryInformation repository;
  private final Object repositoryInstance;
  private final ResourceMetadata resource;
  private final TypeResolver typeResolver;
  private final ResourceMappings mappings;
  private final PersistentEntities entities;
  private final Associations associations;
  private URI basePath;

  public EntityContext(
      TypeResolver typeResolver,
      RepositoryRestConfiguration configuration,
      RepositoryInformation repository,
      Object repositoryInstance,
      ResourceMetadata resource,
      ResourceMappings mappings,
      PersistentEntities entities,
      Associations associations) {

    this.configuration = configuration;
    this.repository = repository;
    this.repositoryInstance = repositoryInstance;
    this.resource = resource;
    this.typeResolver = typeResolver;
    this.basePath = configuration.getBaseUri();
    this.mappings = mappings;
    this.entities = entities;
    this.associations = associations;
  }


  public List<RequestHandler> requestHandlers() {
    final List<RequestHandler> handlers = newArrayList();
    final PersistentEntity<?, ?> entity = entities.getPersistentEntity(resource.getDomainType());
    CrudMethods crudMethods = repository.getCrudMethods();
    if (crudMethods.hasSaveMethod()) {
      HandlerMethod handler = new HandlerMethod(
          repositoryInstance,
          crudMethods.getSaveMethod());
      ActionSpecification put = saveActionSpecification(
          entity,
          RequestMethod.PUT,
          String.format("%s%s/{id}", configuration.getBasePath(), resource.getPath()),
          handler
      );
      handlers.add(new SpringDataRestRequestHandler(this, put));
      ActionSpecification post = saveActionSpecification(
          entity,
          RequestMethod.POST,
          String.format("%s%s", configuration.getBasePath(), resource.getPath()),
          handler
      );
      handlers.add(new SpringDataRestRequestHandler(this, post));
    }
    if (crudMethods.hasDelete()) {
      HandlerMethod handler = new HandlerMethod(
          repositoryInstance,
          crudMethods.getDeleteMethod());
      ActionSpecification spec = new ActionSpecification(
          actionName(entity, crudMethods.getDeleteMethod()),
          String.format("%s%s/{id}", basePath, resource.getPath()),
          newHashSet(RequestMethod.DELETE),
          new HashSet<MediaType>(),
          new HashSet<MediaType>(),
          handler,
          newArrayList(new ResolvedMethodParameter(
              0,
              "id",
              pathAnnotations("id", handler),
              typeResolver.resolve(repository.getIdType()))),
          typeResolver.resolve(Void.TYPE));
      handlers.add(new SpringDataRestRequestHandler(this, spec));
    }
    if (crudMethods.hasFindOneMethod()) {
      HandlerMethod handler = new HandlerMethod(
          repositoryInstance,
          crudMethods.getFindOneMethod());
      ActionSpecification spec = new ActionSpecification(
          actionName(entity, crudMethods.getFindOneMethod()),
          String.format("%s%s/{id}", configuration.getBasePath(), resource.getPath()),
          newHashSet(RequestMethod.GET),
          new HashSet<MediaType>(),
          new HashSet<MediaType>(),
          handler,
          newArrayList(new ResolvedMethodParameter(
              0,
              "id",
              pathAnnotations("id", handler),
              typeResolver.resolve(repository.getIdType()))),
          typeResolver.resolve(Resource.class, repository.getReturnedDomainClass(handler.getMethod())));
      handlers.add(new SpringDataRestRequestHandler(this, spec));
    }
    if (crudMethods.hasFindAllMethod()) {
      HandlerMethod handler = new HandlerMethod(
          repositoryInstance,
          crudMethods.getFindAllMethod());
      ActionSpecification spec = new ActionSpecification(
          actionName(entity, crudMethods.getFindAllMethod()),
          String.format("%s%s", configuration.getBasePath(), resource.getPath()),
          newHashSet(RequestMethod.GET),
          newHashSet(RestMediaTypes.SPRING_DATA_COMPACT_JSON, RestMediaTypes.TEXT_URI_LIST),
          new HashSet<MediaType>(),
          handler,
          findAllParameters(),
          typeResolver.resolve(Resources.class, repository.getReturnedDomainClass(handler.getMethod())));
      handlers.add(new SpringDataRestRequestHandler(this, spec));
    }
    HandlerMethodResolver methodResolver = new HandlerMethodResolver(typeResolver);
    SearchResourceMappings searchMappings = mappings.getSearchResourceMappings(repository.getDomainType());
    for (MethodResourceMapping mapping : searchMappings.getExportedMappings()) {
      HandlerMethod handler = new HandlerMethod(
          repositoryInstance,
          mapping.getMethod());
      ActionSpecification spec = new ActionSpecification(
          actionName(entity, mapping.getMethod()),
          String.format("%s%s/search%s", configuration.getBasePath(), resource.getPath(), mapping.getPath()),
          newHashSet(RequestMethod.GET),
          new HashSet<MediaType>(),
          new HashSet<MediaType>(),
          handler,
          methodResolver.methodParameters(handler),
          inferReturnType(methodResolver, handler));
      handlers.add(new SpringDataRestRequestHandler(this, spec));
    }


    final EntityContext entityContext = this;
    entity.doWithAssociations(new SimpleAssociationHandler() {

      @Override
      public void doWithAssociation(Association<? extends PersistentProperty<?>> association) {
        ResourceMetadata metadata = associations.getMetadataFor(entity.getType());
        PersistentProperty<?> property = association.getInverse();

        if (!associations.isLinkableAssociation(property)) {
          return;
        }

        ResourceMapping mapping = metadata.getMappingFor(property);
        if (property.isWritable() && property.getOwner().equals(entity)) {
          ActionSpecification update = new ActionSpecification(
              String.format("%s%s", lowerCamelCaseName(entity.getType().getSimpleName()), upperCamelCaseName(property
                  .getName())),
              String.format("%s%s/{id}/%s",
                  configuration.getBasePath(),
                  resource.getPath(),
                  mapping.getPath()),
              newHashSet(RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.POST),
              new HashSet<MediaType>(),
              newHashSet(RestMediaTypes.TEXT_URI_LIST, RestMediaTypes.SPRING_DATA_COMPACT_JSON),
              null,
              newArrayList(new ResolvedMethodParameter(
                      0,
                      "id",
                      pathAnnotations("id"),
                      typeResolver.resolve(repository.getIdType())),
                  new ResolvedMethodParameter(
                      0,
                      "body",
                      bodyAnnotations(),
                      property.isCollectionLike()
                      ? typeResolver.resolve(List.class, String.class)
                      : typeResolver.resolve(String.class))),
              propertyResponse(property));
          handlers.add(new SpringDataRestRequestHandler(entityContext, update));

        }

        ActionSpecification get = new ActionSpecification(
            String.format("%s%s", lowerCamelCaseName(entity.getType().getSimpleName()), upperCamelCaseName(property
                .getName())),
            String.format("%s%s/{id}/%s",
                configuration.getBasePath(),
                resource.getPath(),
                mapping.getPath()),
            newHashSet(RequestMethod.GET),
            newHashSet(HAL_JSON),
            new HashSet<MediaType>(),
            null,
            newArrayList(new ResolvedMethodParameter(
                0,
                "id",
                pathAnnotations("id"),
                typeResolver.resolve(repository.getIdType()))),
            propertyResponse(property));
        handlers.add(new SpringDataRestRequestHandler(entityContext, get));

        ActionSpecification delete = new ActionSpecification(
            String.format("%s%s", lowerCamelCaseName(entity.getType().getSimpleName()), upperCamelCaseName(property
                .getName())),
            String.format("%s%s/{id}/%s",
                configuration.getBasePath(),
                resource.getPath(),
                mapping.getPath()),
            newHashSet(RequestMethod.DELETE),
            new HashSet<MediaType>(),
            newHashSet(RestMediaTypes.TEXT_URI_LIST, RestMediaTypes.SPRING_DATA_COMPACT_JSON),
            null,
            newArrayList(new ResolvedMethodParameter(
                0,
                "id",
                pathAnnotations("id"),
                typeResolver.resolve(repository.getIdType()))),
            typeResolver.resolve(Void.TYPE));
        handlers.add(new SpringDataRestRequestHandler(entityContext, delete));

        if (property.isMap() || property.isCollectionLike()) {
          String propertyIdentifier = propertyIdentifierName(property);
          ActionSpecification getPropertyItem = new ActionSpecification(
              String.format("%s%s",
                  lowerCamelCaseName(entity.getType().getSimpleName()),
                  upperCamelCaseName(property.getName())),
              String.format("%s%s/{id}/%s/{%s}",
                  configuration.getBasePath(),
                  resource.getPath(),
                  mapping.getPath(),
                  propertyIdentifier),
              newHashSet(RequestMethod.GET),
              new HashSet<MediaType>(),
              newHashSet(HAL_JSON),
              null,
              newArrayList(new ResolvedMethodParameter(
                      0,
                      "id",
                      pathAnnotations("id"),
                      typeResolver.resolve(repository.getIdType())),
                  new ResolvedMethodParameter(
                      1,
                      propertyIdentifier,
                      pathAnnotations(propertyIdentifier),
                      typeResolver.resolve(String.class))),
              propertyItemResponse(property));
          handlers.add(new SpringDataRestRequestHandler(entityContext, getPropertyItem));

          ActionSpecification deleteItem = new ActionSpecification(
              String.format("%s%s", lowerCamelCaseName(entity.getType().getSimpleName()), upperCamelCaseName(property
                  .getName())),
              String.format("%s%s/{id}/%s/{%s}",
                  configuration.getBasePath(),
                  resource.getPath(),
                  mapping.getPath(),
                  propertyIdentifier),
              newHashSet(RequestMethod.DELETE),
              new HashSet<MediaType>(),
              newHashSet(RestMediaTypes.TEXT_URI_LIST, RestMediaTypes.SPRING_DATA_COMPACT_JSON),
              null,
              newArrayList(new ResolvedMethodParameter(
                      0,
                      "id",
                      pathAnnotations("id"),
                      typeResolver.resolve(repository.getIdType())),
                  new ResolvedMethodParameter(
                      1,
                      propertyIdentifier,
                      pathAnnotations(propertyIdentifier),
                      typeResolver.resolve(String.class))),
              typeResolver.resolve(Void.TYPE));
          handlers.add(new SpringDataRestRequestHandler(entityContext, deleteItem));
        }
      }
    });
    return handlers;
  }

  private String propertyIdentifierName(PersistentProperty<?> property) {
    String propertyName = property.getName();
    if (property.isCollectionLike()) {
      propertyName = property.getComponentType().getSimpleName();
    } else if (property.isMap()) {
      propertyName = property.getMapValueType().getSimpleName();
    }
    return String.format("%sId", propertyName.toLowerCase());
  }

  private ResolvedType propertyResponse(PersistentProperty<?> property) {
    if (property.isCollectionLike()) {
      return typeResolver.resolve(Resources.class, property.getComponentType());
    } else if (property.isMap()) {
      return typeResolver.resolve(
          Resource.class,
          typeResolver.resolve(
              Map.class,
              String.class,
              property.getMapValueType()));
    }
    return typeResolver.resolve(Resource.class, property.getType());
  }

  private ResolvedType propertyItemResponse(PersistentProperty<?> property) {
    if (property.isCollectionLike()) {
      return typeResolver.resolve(Resource.class, property.getComponentType());
    } else if (property.isMap()) {
      return typeResolver.resolve(
          Resource.class,
          property.getMapValueType());
    }
    return typeResolver.resolve(Resource.class, property.getType());
  }

  private ResolvedType inferReturnType(HandlerMethodResolver methodResolver, HandlerMethod handler) {
    ResolvedType returnType = methodResolver.methodReturnType(handler);
    if (Collections.isContainerType(returnType)) {
      return typeResolver.resolve(Resources.class, returnType);
    } else if (Types.isBaseType(returnType)) {
      return returnType;
    }
    return typeResolver.resolve(Resource.class, returnType);
  }

  private String lowerCamelCaseName(String stringValue) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, stringValue);
  }

  private String upperCamelCaseName(String stringValue) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, stringValue);
  }

  private String actionName(PersistentEntity<?, ?> entity, Method method) {
    return String.format("%s%s", method.getName(), entity.getType().getSimpleName());
  }

  private ActionSpecification saveActionSpecification(PersistentEntity<?, ?> entity, RequestMethod method, String
      path, HandlerMethod handler) {
    return new ActionSpecification(
        actionName(entity, handler.getMethod()),
        path,
        newHashSet(method),
        new HashSet<MediaType>(),
        new HashSet<MediaType>(),
        handler,
        newArrayList(
            new ResolvedMethodParameter(
                0,
                "id",
                pathAnnotations("id", handler),
                typeResolver.resolve(repository.getIdType())),
            new ResolvedMethodParameter(
                0,
                "body",
                bodyAnnotations(handler),
                typeResolver.resolve(repository.getDomainType()))),
        typeResolver.resolve(Resource.class, repository.getReturnedDomainClass(handler.getMethod())));
  }

  private List<Annotation> pathAnnotations(String name) {
    return pathAnnotations(name, null);
  }

  private List<Annotation> pathAnnotations(String name, HandlerMethod handler) {
    List<Annotation> annotations = handlerAnnotations(handler);
    if (name != null) {
      annotations.add(SynthesizedAnnotations.pathVariable(name));
    }
    return annotations;
  }

  private List<Annotation> handlerAnnotations(HandlerMethod handler) {
    List<Annotation> annotations = new ArrayList<Annotation>();
    if (handler != null) {
      annotations.addAll(Arrays.asList(AnnotationUtils.getAnnotations(handler.getMethod())));
    }
    return annotations;
  }

  private List<Annotation> pathAnnotations() {
    return pathAnnotations(null, null);
  }

  private List<Annotation> bodyAnnotations(HandlerMethod handler) {
    List<Annotation> annotations = handlerAnnotations(handler);
    annotations.add(SynthesizedAnnotations.REQUEST_BODY_ANNOTATION);
    return annotations;
  }

  private List<Annotation> bodyAnnotations() {
    List<Annotation> annotations = handlerAnnotations(null);
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
