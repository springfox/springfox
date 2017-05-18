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
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.rest.core.mapping.ResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.data.rest.webmvc.mapping.Associations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;

import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static org.springframework.data.rest.webmvc.RestMediaTypes.*;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.*;

public class EntityPropertiesExtractor implements RequestHandlerExtractor {
  @Override
  public List<RequestHandler> extract(final EntityContext context) {
    final List<RequestHandler> handlers = newArrayList();
    final PersistentEntity<?, ?> entity = context.entity();
    final Associations associations = context.getAssociations();
    final TypeResolver resolver = context.getTypeResolver();
    final RepositoryMetadata repository = context.getRepositoryMetadata();

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
                  context.basePath(),
                  context.resourcePath(),
                  mapping.getPath()),
              newHashSet(RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.POST),
              new HashSet<MediaType>(),
              newHashSet(RestMediaTypes.TEXT_URI_LIST, RestMediaTypes.SPRING_DATA_COMPACT_JSON),
              null,
              newArrayList(new ResolvedMethodParameter(
                      0,
                      "id",
                      pathAnnotations("id"),
                      resolver.resolve(repository.getIdType())),
                  new ResolvedMethodParameter(
                      0,
                      "body",
                      bodyAnnotations(),
                      property.isCollectionLike()
                      ? resolver.resolve(List.class, String.class)
                      : resolver.resolve(String.class))),
              propertyResponse(property, resolver));
          handlers.add(new SpringDataRestRequestHandler(context, update));

        }

        ActionSpecification get = new ActionSpecification(
            String.format("%s%s", lowerCamelCaseName(entity.getType().getSimpleName()), upperCamelCaseName(property
                .getName())),
            String.format("%s%s/{id}/%s",
                context.basePath(),
                context.resourcePath(),
                mapping.getPath()),
            newHashSet(RequestMethod.GET),
            newHashSet(HAL_JSON),
            new HashSet<MediaType>(),
            null,
            newArrayList(new ResolvedMethodParameter(
                0,
                "id",
                pathAnnotations("id"),
                resolver.resolve(repository.getIdType()))),
            propertyResponse(property, resolver));
        handlers.add(new SpringDataRestRequestHandler(context, get));

        ActionSpecification delete = new ActionSpecification(
            String.format("%s%s", lowerCamelCaseName(entity.getType().getSimpleName()), upperCamelCaseName(property
                .getName())),
            String.format("%s%s/{id}/%s",
                context.basePath(),
                context.resourcePath(),
                mapping.getPath()),
            newHashSet(RequestMethod.DELETE),
            new HashSet<MediaType>(),
            newHashSet(RestMediaTypes.TEXT_URI_LIST, RestMediaTypes.SPRING_DATA_COMPACT_JSON),
            null,
            newArrayList(new ResolvedMethodParameter(
                0,
                "id",
                pathAnnotations("id"),
                resolver.resolve(repository.getIdType()))),
            resolver.resolve(Void.TYPE));
        handlers.add(new SpringDataRestRequestHandler(context, delete));

        if (property.isMap() || property.isCollectionLike()) {
          String propertyIdentifier = propertyIdentifierName(property);
          ActionSpecification getPropertyItem = new ActionSpecification(
              String.format("%s%s",
                  lowerCamelCaseName(entity.getType().getSimpleName()),
                  upperCamelCaseName(property.getName())),
              String.format("%s%s/{id}/%s/{%s}",
                  context.basePath(),
                  context.resourcePath(),
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
                      resolver.resolve(repository.getIdType())),
                  new ResolvedMethodParameter(
                      1,
                      propertyIdentifier,
                      pathAnnotations(propertyIdentifier),
                      resolver.resolve(String.class))),
              propertyItemResponse(property, resolver));
          handlers.add(new SpringDataRestRequestHandler(context, getPropertyItem));

          ActionSpecification deleteItem = new ActionSpecification(
              String.format("%s%s", lowerCamelCaseName(entity.getType().getSimpleName()), upperCamelCaseName(property
                  .getName())),
              String.format("%s%s/{id}/%s/{%s}",
                  context.basePath(),
                  context.resourcePath(),
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
                      resolver.resolve(repository.getIdType())),
                  new ResolvedMethodParameter(
                      1,
                      propertyIdentifier,
                      pathAnnotations(propertyIdentifier),
                      resolver.resolve(String.class))),
              resolver.resolve(Void.TYPE));
          handlers.add(new SpringDataRestRequestHandler(context, deleteItem));
        }
      }
    });
    return handlers;
  }

}
