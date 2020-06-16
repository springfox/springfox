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
import springfox.documentation.spring.data.rest.SpecificationBuilder.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static springfox.documentation.spring.data.rest.SpecificationBuilder.*;

class EntityDeleteExtractor implements EntityOperationsExtractor {
  @Override
  public List<RequestHandler> extract(EntityContext context) {
    List<RequestHandler> handlers = new ArrayList<>();

    context.crudMethods().getDeleteMethod()
        .map(method -> new HandlerMethod(context.getRepositoryInstance(), method))
        .ifPresent(handler -> {

          entityAction(context, handler)
              .path(String.format("%s%s/{id}",
                                  context.basePath(),
                                  context.resourcePath()))
              .supportsMethod(DELETE)
              .parameterType(ParameterType.ID)
              .build()
              .map(get -> new SpringDataRestRequestHandler(context, get))
              .ifPresent(handlers::add);

        });

    return handlers;
  }
}
