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
import org.springframework.data.rest.core.mapping.MethodResourceMapping;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.schema.Collections;
import springfox.documentation.schema.Types;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.spring.data.rest.RequestExtractionUtils.*;

class EntitySearchExtractor implements EntityOperationsExtractor {
  @Override
  public List<RequestHandler> extract(EntityContext context) {
    final List<RequestHandler> handlers = newArrayList();
    final PersistentEntity<?, ?> entity = context.entity();
    HandlerMethodResolver methodResolver = new HandlerMethodResolver(context.getTypeResolver());
    SearchResourceMappings searchMappings = context.searchMappings();
    for (MethodResourceMapping mapping : searchMappings.getExportedMappings()) {
      HandlerMethod handler = new HandlerMethod(
          context.getRepositoryInstance(),
          mapping.getMethod());
      ActionSpecification spec = new ActionSpecification(
          actionName(entity, mapping.getMethod()),
          String.format("%s%s/search%s",
              context.basePath(),
              context.resourcePath(),
              mapping.getPath()),
          newHashSet(RequestMethod.GET),
          new HashSet<MediaType>(),
          new HashSet<MediaType>(),
          handler,
          methodResolver.methodParameters(handler),
          inferReturnType(methodResolver, handler, context.getTypeResolver()));
      handlers.add(new SpringDataRestRequestHandler(context, spec));
    }
    return handlers;
  }


  private ResolvedType inferReturnType(
      HandlerMethodResolver methodResolver,
      HandlerMethod handler,
      TypeResolver resolver) {
    ResolvedType returnType = methodResolver.methodReturnType(handler);
    if (Collections.isContainerType(returnType)) {
      return resolver.resolve(Resources.class, returnType);
    } else if (Types.isBaseType(returnType)) {
      return returnType;
    }
    return resolver.resolve(Resource.class, returnType);
  }
}
