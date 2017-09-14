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
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.rest.core.mapping.ResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.*;

public class EntityAssociationDeleteExtractor implements EntityAssociationOperationsExtractor {
  @Override
  public List<RequestHandler> extract(EntityAssociationContext context) {
    List<RequestHandler> handlers = new ArrayList<RequestHandler>();
    ResourceMetadata metadata = context.associationMetadata();
    Association<? extends PersistentProperty<?>> association = context.getAssociation();
    PersistentProperty<?> property = association.getInverse();
    ResourceMapping mapping = metadata.getMappingFor(property);
    EntityContext entityContext = context.getEntityContext();
    PersistentEntity entity = entityContext.entity();
    TypeResolver resolver = entityContext.getTypeResolver();
    RepositoryMetadata repository = entityContext.getRepositoryMetadata();

    ActionSpecification delete = new ActionSpecification(
        String.format("%s%s",
            lowerCamelCaseName(entity.getType().getSimpleName()),
            upperCamelCaseName(property.getName())),
        String.format("%s%s/{id}/%s",
            entityContext.basePath(),
            entityContext.resourcePath(),
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
    handlers.add(new SpringDataRestRequestHandler(entityContext, delete));
    return handlers;
  }
}
