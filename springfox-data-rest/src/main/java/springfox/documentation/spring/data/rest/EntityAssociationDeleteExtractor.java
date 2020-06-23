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

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.rest.core.Path;
import org.springframework.data.rest.core.mapping.ResourceMapping;
import springfox.documentation.RequestHandler;
import springfox.documentation.spring.data.rest.SpecificationBuilder.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.rest.webmvc.RestMediaTypes.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static springfox.documentation.spring.data.rest.SpecificationBuilder.*;

public class EntityAssociationDeleteExtractor implements EntityAssociationOperationsExtractor {
  @Override
  public List<RequestHandler> extract(EntityAssociationContext context) {

    List<RequestHandler> handlers = new ArrayList<>();
    PersistentProperty<?> property = context.getAssociation().getInverse();

    String mappingPath = context.associationMetadata()
        .map(metadata -> metadata.getMappingFor(property))
        .map(ResourceMapping::getPath)
        .map(Path::toString)
        .orElse("");

    String path = String.format("%s%s/{id}/%s",
        context.getEntityContext().basePath(),
        context.getEntityContext().resourcePath(),
        mappingPath);

    associationAction(context, path)
        .supportsMethod(DELETE)
        .consumes(TEXT_URI_LIST)
        .consumes(SPRING_DATA_COMPACT_JSON)
        .parameterType(ParameterType.ID)
        .build()
        .map(delete -> new SpringDataRestRequestHandler(context.getEntityContext(), delete))
        .ifPresent(handlers::add);

    return handlers;
  }
}
