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

import org.springframework.data.mapping.PersistentProperty;
import springfox.documentation.RequestHandler;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.rest.webmvc.RestMediaTypes.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import springfox.documentation.spring.data.rest.SpecificationBuilder.Parameter;

public class EntityAssociationGetExtractor implements EntityAssociationOperationsExtractor {
  @Override
  public List<RequestHandler> extract(EntityAssociationContext context) {

    final List<RequestHandler> handlers = new ArrayList<>();
    final PersistentProperty<?> property = context.getAssociation().getInverse();

    final String mappingPath = context.associationMetadata()
      .map(metadata -> metadata.getMappingFor(property))
      .map(mapping -> mapping.getPath())
      .map(p -> p.toString())
      .orElse("");

    final String path = String.format("%s%s/{id}/%s",
                                      context.getEntityContext().basePath(),
                                      context.getEntityContext().resourcePath(),
                                      mappingPath);

    SpecificationBuilder.getInstance(context, path)
      .supportsMethod(GET)
      .produces(HAL_JSON)
      .withParameter(Parameter.ID)
      .build()
      .map(get -> new SpringDataRestRequestHandler(context.getEntityContext(), get))
      .ifPresent(handlers::add);

    return handlers;
  }
}
