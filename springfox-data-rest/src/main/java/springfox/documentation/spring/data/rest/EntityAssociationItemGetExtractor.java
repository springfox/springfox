/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.rest.core.mapping.ResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.springframework.data.rest.webmvc.RestMediaTypes.*;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.*;

public class EntityAssociationItemGetExtractor implements EntityAssociationOperationsExtractor {
  @Override
  public List<RequestHandler> extract(EntityAssociationContext context) {
    List<RequestHandler> handlers = new ArrayList<>();
    ResourceMetadata metadata = context.associationMetadata();
    Association<? extends PersistentProperty<?>> association = context.getAssociation();
    PersistentProperty<?> property = association.getInverse();
    ResourceMapping mapping = metadata.getMappingFor(property);
    EntityContext entityContext = context.getEntityContext();
    PersistentEntity entity = entityContext.entity();
    TypeResolver resolver = entityContext.getTypeResolver();
    RepositoryMetadata repository = entityContext.getRepositoryMetadata();
    if (property.isMap() || property.isCollectionLike()) {
      String propertyIdentifier = propertyIdentifierName(property);
      ActionSpecification getPropertyItem = new ActionSpecification(
          String.format("%s%s",
              lowerCamelCaseName(entity.getType().getSimpleName()),
              upperCamelCaseName(property.getName())),
          String.format("%s%s/{id}/%s/{%s}",
              entityContext.basePath(),
              entityContext.resourcePath(),
              mapping.getPath(),
              propertyIdentifier),
              singleton(RequestMethod.GET),
          new HashSet<>(),
              singleton(HAL_JSON),
          null,
          Stream.of(new ResolvedMethodParameter(
                  0,
                  "id",
                  pathAnnotations("id"),
                  resolver.resolve(repository.getIdType())),
              new ResolvedMethodParameter(
                  1,
                  propertyIdentifier,
                  pathAnnotations(propertyIdentifier),
                  resolver.resolve(String.class))).collect(toList()),
          propertyItemResponse(property, resolver));
      handlers.add(new SpringDataRestRequestHandler(entityContext, getPropertyItem));
    }
    return handlers;
  }
}
