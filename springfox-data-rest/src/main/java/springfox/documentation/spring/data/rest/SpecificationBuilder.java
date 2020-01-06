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
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.schema.Types;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.*;

abstract class SpecificationBuilder {

  private final Set<RequestMethod> supportedMethods = new HashSet<>();
  private final Set<MediaType> produces = new HashSet<>();
  private final Set<MediaType> consumes = new HashSet<>();
  private final List<ResolvedMethodParameter> parameters = new ArrayList<>();
  private String path;

  static ResolvedType resolveType(EntityContext context, Function<RepositoryMetadata, Type> getType) {

    RepositoryMetadata repository = context.getRepositoryMetadata();
    TypeResolver typeResolver = context.getTypeResolver();

    return getType != null
           ? typeResolver.resolve(getType.apply(repository))
           : typeResolver.resolve(Void.TYPE);
  }

  static SpecificationBuilder entityAction(EntityContext context, HandlerMethod handlerMethod) {
    return new EntityActionSpecificationBuilder(context, handlerMethod);
  }

  static SpecificationBuilder associationAction(EntityAssociationContext context, String path) {
    return new AssociationActionSpecificationBuilder(context, path);
  }

  SpecificationBuilder withPath(String path) {
    this.path = path;
    return this;
  }

  SpecificationBuilder supportsMethod(RequestMethod method) {
    this.supportedMethods.add(method);
    return this;
  }

  SpecificationBuilder produces(MediaType type) {
    this.produces.add(type);
    return this;
  }

  SpecificationBuilder consumes(MediaType type) {
    this.consumes.add(type);
    return this;
  }

  SpecificationBuilder withParameter(ResolvedMethodParameter parameter) {
    this.parameters.add(parameter);
    return this;
  }

  public Set<RequestMethod> getSupportedMethods() {
    return supportedMethods;
  }

  public Set<MediaType> getProduces() {
    return produces;
  }

  public Set<MediaType> getConsumes() {
    return consumes;
  }

  public List<ResolvedMethodParameter> getParameters() {
    return parameters;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  abstract SpecificationBuilder withParameterType(ParameterType parameterType);

  abstract Optional<ActionSpecification> build();

  enum ParameterType {
    ID,
    RESOURCE,
    PAGEABLE_RESOURCE,
    ASSOCIATION
  }

  private static class AssociationActionSpecificationBuilder extends SpecificationBuilder {

    private final EntityAssociationContext context;
    private final PersistentProperty<?> property;

    AssociationActionSpecificationBuilder(EntityAssociationContext context, String path) {
      setPath(path);
      this.context = context;
      this.property = context.getAssociation().getInverse();
    }

    @Override
    SpecificationBuilder withParameterType(ParameterType parameterType) {

      int index = this.getParameters().size();

      switch (parameterType) {
        case ID:
          return withParameter(new ResolvedMethodParameter(
              0,
              "id",
              pathAnnotations("id"),
              resolveType(context.getEntityContext(), RepositoryMetadata::getIdType)));

        case RESOURCE:
          return withParameter(new ResolvedMethodParameter(
              0,
              "body",
              bodyAnnotations(),
              property.isCollectionLike()
              ? context.getEntityContext().getTypeResolver().resolve(List.class, String.class)
              : context.getEntityContext().getTypeResolver().resolve(String.class)));

        case ASSOCIATION:
          return withParameter(new ResolvedMethodParameter(
              index,
              propertyIdentifierName(property),
              pathAnnotations(propertyIdentifierName(property)),
              context.getEntityContext().getTypeResolver().resolve(String.class)));

        case PAGEABLE_RESOURCE:
        default:
          break;
      }
      return this;
    }

    @Override
    Optional<ActionSpecification> build() {

      TypeResolver resolver = context.getEntityContext().getTypeResolver();

      return context.getEntityContext().entity()
          .map(entity -> actionName(entity, property))
          .map(actionName -> new ActionSpecification(
              actionName,
              getPath(),
              getSupportedMethods(),
              getProduces(),
              getConsumes(),
              null,
              getType(),
              getParameters(),
              returnType(resolver))
          );
    }

    private String actionName(
        PersistentEntity<?, ?> entity,
        PersistentProperty<?> property) {

      return String.format("%s%s",
          lowerCamelCaseName(entity.getType().getSimpleName()),
          upperCamelCaseName(property.getName()));
    }

    private Class<?> getType() {
      return context.getEntityContext().entity().get().getType();
    }

    private ResolvedType returnType(TypeResolver resolver) {
      return getSupportedMethods().contains(DELETE)
             ? resolver.resolve(Void.TYPE)
             : getParameters().stream()
                   .anyMatch(param -> param.getParameterIndex() > 0)
               ? propertyItemResponse(property, resolver)
               : propertyResponse(property, resolver);
    }
  }

  private static class EntityActionSpecificationBuilder extends SpecificationBuilder {

    private final EntityContext context;
    private final HandlerMethod handlerMethod;

    EntityActionSpecificationBuilder(EntityContext context, HandlerMethod handlerMethod) {
      this.context = context;
      this.handlerMethod = handlerMethod;
    }

    private static ResolvedMethodParameter transferResolvedMethodParameter(
        ResolvedMethodParameter src) {
      Optional<Param> param = src.findAnnotation(Param.class);
      if (param.isPresent()) {
        return src.annotate(SynthesizedAnnotations.requestParam(param.get().value()));
      }
      return src;
    }

    @Override
    SpecificationBuilder withParameterType(ParameterType parameterType) {

      switch (parameterType) {
        case ID:
          return withParameter(new ResolvedMethodParameter(
              0,
              "id",
              pathAnnotations("id", handlerMethod),
              resolveType(context, RepositoryMetadata::getIdType)));

        case RESOURCE:
          return withParameter(new ResolvedMethodParameter(
              0,
              "body",
              bodyAnnotations(handlerMethod),
              resolveType(context, RepositoryMetadata::getDomainType)));


        case PAGEABLE_RESOURCE:
          RepositoryRestConfiguration configuration = context.getConfiguration();
          TypeResolver typeResolver = context.getTypeResolver();

          //noinspection unchecked
          withParameter(new ResolvedMethodParameter(
              0,
              configuration.getPageParamName(),
              Collections.EMPTY_LIST,
              typeResolver.resolve(Integer.class)));
          //noinspection unchecked
          withParameter(new ResolvedMethodParameter(
              1,
              configuration.getLimitParamName(),
              Collections.EMPTY_LIST,
              typeResolver.resolve(Integer.class)));
          //noinspection unchecked
          withParameter(new ResolvedMethodParameter(
              2,
              configuration.getSortParamName(),
              Collections.EMPTY_LIST,
              typeResolver.resolve(String.class)));

        default:
          break;
      }
      return this;
    }

    @Override
    Optional<ActionSpecification> build() {

      if (!StringUtils.hasText(getPath())) {
        setPath(String.format("%s%s",
            context.basePath(),
            context.resourcePath()));
      }

      return context.entity()
          .map(entity -> actionName(entity, handlerMethod.getMethod()))
          .map(actionName -> new ActionSpecification(
              actionName,
              getPath(),
              getSupportedMethods(),
              getProduces(),
              getConsumes(),
              handlerMethod,
              getType(),
              inputParameters(),
              inferReturnType(context, handlerMethod))
          );
    }

    private ResolvedType inferReturnType(
        EntityContext context,
        HandlerMethod handler) {

      TypeResolver resolver = context.getTypeResolver();
      HandlerMethodResolver methodResolver = new HandlerMethodResolver(resolver);
      RepositoryMetadata repository = context.getRepositoryMetadata();

      ResolvedType domainReturnType =
          resolver.resolve(repository.getReturnedDomainClass(handler.getMethod()));
      ResolvedType methodReturnType =
          methodResolver.methodReturnType(handler);

      if (isContainerType(methodReturnType)) {
        return resolver.resolve(CollectionModel.class,
            collectionElementType(methodReturnType));
      } else if (Iterable.class.isAssignableFrom(methodReturnType.getErasedType())) {
        return resolver.resolve(CollectionModel.class, domainReturnType);
      } else if (Types.isBaseType(domainReturnType)) {
        return domainReturnType;
      } else if (Types.isVoid(domainReturnType)) {
        return resolver.resolve(Void.TYPE);
      }

      return resolver.resolve(EntityModel.class, domainReturnType);
    }

    private List<ResolvedMethodParameter> transferResolvedMethodParameterList(
        EntityContext context,
        HandlerMethod handler) {

      TypeResolver resolver = context.getTypeResolver();
      HandlerMethodResolver methodResolver = new HandlerMethodResolver(resolver);

      return methodResolver.methodParameters(handler).stream()
          .map(EntityActionSpecificationBuilder::transferResolvedMethodParameter)
          .collect(Collectors.toList());
    }

    private Class<?> getType() {
      return context.entity().get().getType();
    }

    private List<ResolvedMethodParameter> inputParameters() {
      return !getParameters().isEmpty()
             ? getParameters()
             : transferResolvedMethodParameterList(context, handlerMethod);
    }
  }
}
