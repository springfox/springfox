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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.schema.Types;
import springfox.documentation.service.ResolvedMethodParameter;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.actionName;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.bodyAnnotations;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.lowerCamelCaseName;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.pathAnnotations;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.propertyIdentifierName;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.propertyItemResponse;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.propertyResponse;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.upperCamelCaseName;

abstract class SpecificationBuilder {

  protected final Set<RequestMethod> supportedMethods = new HashSet<>();
  protected final Set<MediaType> produces = new HashSet<>();
  protected final Set<MediaType> consumes = new HashSet<>();
  protected final List<ResolvedMethodParameter> parameters = new ArrayList<>();
  protected String path;

  enum Parameter {

    ID,
    BODY,
    PAGEABLE,
    ITEM;
  }

  private static class AssociationActionSpecificationBuilder extends SpecificationBuilder {

    private final EntityAssociationContext context;
    private final PersistentProperty<?> property;

    protected AssociationActionSpecificationBuilder(EntityAssociationContext context, String path) {
      this.context = context;
      this.path = path;
      this.property = context.getAssociation().getInverse();
    }

    @Override
    SpecificationBuilder withParameter(Parameter parameter) {

      int index = this.parameters.size();

      switch (parameter) {
        case ID:

          this.parameters.add(new ResolvedMethodParameter(
            0,
            "id",
            pathAnnotations("id"),
            resolveType(context.getEntityContext(), repo -> repo.getIdType())));
          break;

        case BODY:

          this.parameters.add(new ResolvedMethodParameter(
            0,
            "body",
            bodyAnnotations(),
            property.isCollectionLike()
              ? context.getEntityContext().getTypeResolver().resolve(List.class, String.class)
              : context.getEntityContext().getTypeResolver().resolve(String.class)));
          break;

        case ITEM:
          this.parameters.add(new ResolvedMethodParameter(
            index,
            propertyIdentifierName(property),
            pathAnnotations(propertyIdentifierName(property)),
            context.getEntityContext().getTypeResolver().resolve(String.class)));

          break;

        case PAGEABLE:
          break;
        default: break;
      }
      return this;
    }

    @Override
    Optional<ActionSpecification> build() {

      final TypeResolver resolver = context.getEntityContext().getTypeResolver();

      return context.getEntityContext().entity()
        .map(entity -> actionName(entity, property))
        .map(actionName -> new ActionSpecification(
          actionName,
          path,
          supportedMethods,
          produces,
          consumes,
          null,
          parameters,
          supportedMethods.contains(DELETE)
            ? resolver.resolve(Void.TYPE)
            : parameters.stream().anyMatch(param -> param.getParameterIndex() > 0)
              ? propertyItemResponse(property, resolver)
              : propertyResponse(property, resolver))
        );

    }

    private static String actionName(PersistentEntity<?, ?> entity, PersistentProperty<?> property) {
      return String.format("%s%s",
                           lowerCamelCaseName(entity.getType().getSimpleName()),
                           upperCamelCaseName(property.getName()));
    }

  }

  private static class EntityActionSpecificationBuilder extends SpecificationBuilder {

    private final EntityContext context;
    private final HandlerMethod handlerMethod;

    protected EntityActionSpecificationBuilder(EntityContext context, HandlerMethod handlerMethod) {
      this.context = context;
      this.handlerMethod = handlerMethod;
    }

    @Override
    SpecificationBuilder withParameter(Parameter parameter) {

      switch (parameter) {
        case ID:

          this.parameters.add(new ResolvedMethodParameter(
            0,
            "id",
            pathAnnotations("id", handlerMethod),
            resolveType(context, repo -> repo.getIdType())));

          break;

        case BODY:

          this.parameters.add(new ResolvedMethodParameter(
            0,
            "body",
            bodyAnnotations(handlerMethod),
            resolveType(context, repo -> repo.getDomainType())));

          break;

        case PAGEABLE:

          final RepositoryRestConfiguration configuration = context.getConfiguration();
          final TypeResolver typeResolver = context.getTypeResolver();

          this.parameters.add(new ResolvedMethodParameter(
            0,
            configuration.getPageParamName(),
            Collections.EMPTY_LIST,
            typeResolver.resolve(String.class)));
          this.parameters.add(new ResolvedMethodParameter(
            1,
            configuration.getLimitParamName(),
            Collections.EMPTY_LIST,
            typeResolver.resolve(String.class)));
          this.parameters.add(new ResolvedMethodParameter(
            2,
            configuration.getSortParamName(),
            Collections.EMPTY_LIST,
            typeResolver.resolve(String.class)));

          break;
          
        default: break;
      }
      return this;
    }

    @Override
    Optional<ActionSpecification> build() {

      // Default path
      if (!StringUtils.hasText(path)) {
        path = String.format("%s%s",
                             context.basePath(),
                             context.resourcePath());
      }

      return context.entity()
        .map(entity -> actionName(entity, handlerMethod.getMethod()))
        .map(actionName -> new ActionSpecification(
          actionName,
          path,
          supportedMethods,
          produces,
          consumes,
          handlerMethod,
          !parameters.isEmpty() ? parameters : transferResolvedMethodParameterList(context, handlerMethod),
          inferReturnType(context, handlerMethod))
        );
    }

    private static List<ResolvedMethodParameter> transferResolvedMethodParameterList(EntityContext context, HandlerMethod handler) {

      final TypeResolver resolver = context.getTypeResolver();
      final HandlerMethodResolver methodResolver = new HandlerMethodResolver(resolver);

      return methodResolver.methodParameters(handler).stream()
        .map(resolvedMethodParameter -> transferResolvedMethodParameter(resolvedMethodParameter))
        .collect(Collectors.toList());
    }

    private static ResolvedMethodParameter transferResolvedMethodParameter(ResolvedMethodParameter src) {
      com.google.common.base.Optional<Param> param = src.findAnnotation(Param.class);
      if (param.isPresent()) {
        return src.annotate(SynthesizedAnnotations.requestParam(param.get().value()));
      }
      return src;
    }

    private static ResolvedType inferReturnType(
      EntityContext context,
      HandlerMethod handler) {

      final TypeResolver resolver = context.getTypeResolver();
      final HandlerMethodResolver methodResolver = new HandlerMethodResolver(resolver);
      final RepositoryMetadata repository = context.getRepositoryMetadata();

      final ResolvedType domainReturnType = resolver.resolve(repository.getReturnedDomainClass(handler.getMethod()));
      final ResolvedType methodReturnType = methodResolver.methodReturnType(handler);

      if (springfox.documentation.schema.Collections.isContainerType(methodReturnType)) {
        return resolver.resolve(Resources.class, springfox.documentation.schema.Collections.collectionElementType(methodReturnType));
      } else if (Iterable.class.isAssignableFrom(methodReturnType.getErasedType())) {
        return resolver.resolve(Resources.class, domainReturnType);
      } else if (Types.isBaseType(domainReturnType)) {
        return domainReturnType;
      } else if (Types.isVoid(domainReturnType)) {
        return resolver.resolve(Void.TYPE);
      }

      return resolver.resolve(Resource.class, domainReturnType);
    }

  }

  protected static ResolvedType resolveType(EntityContext context, Function<RepositoryMetadata, Type> getType) {

    final RepositoryMetadata repository = context.getRepositoryMetadata();
    final TypeResolver typeResolver = context.getTypeResolver();

    return getType != null ? typeResolver.resolve(getType.apply(repository)) : typeResolver.resolve(Void.TYPE);
  }

  static SpecificationBuilder getInstance(EntityContext context, HandlerMethod handlerMethod) {
    return new EntityActionSpecificationBuilder(context, handlerMethod);
  }

  static SpecificationBuilder getInstance(EntityAssociationContext context, String path) {
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

  abstract SpecificationBuilder withParameter(Parameter parameter);

  abstract Optional<ActionSpecification> build();

}
