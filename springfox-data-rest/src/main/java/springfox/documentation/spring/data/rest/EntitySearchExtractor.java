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

import org.springframework.web.method.HandlerMethod;
import springfox.documentation.RequestHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static springfox.documentation.spring.data.rest.SpecificationBuilder.*;

class EntitySearchExtractor implements EntityOperationsExtractor {
  @Override
  public List<RequestHandler> extract(EntityContext context) {

    return StreamSupport.stream(context.searchMappings().getExportedMappings().spliterator(), false)
        .map(mapping -> {

          final HandlerMethod handler = new HandlerMethod(context.getRepositoryInstance(), mapping.getMethod());

          return entityAction(context, handler)
              .path(String.format("%s%s/search%s",
                                  context.basePath(),
                                  context.resourcePath(),
                                  mapping.getPath()))
              .supportsMethod(GET)
              .build()
              .map(get -> new SpringDataRestRequestHandler(context, get));

        })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }
}
