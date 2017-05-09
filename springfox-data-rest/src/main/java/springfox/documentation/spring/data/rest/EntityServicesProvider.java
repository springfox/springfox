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
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.stereotype.Component;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;

import java.util.List;

import static com.google.common.collect.Lists.*;

@Component
public class EntityServicesProvider implements RequestHandlerProvider {
  private final RepositoryRestConfiguration configuration;
  private final ResourceMappings mappings;
  private final Repositories repositories;
  private final TypeResolver typeResolver;

  public EntityServicesProvider(
      RepositoryRestConfiguration configuration,
      ResourceMappings mappings,
      Repositories repositories,
      TypeResolver typeResolver) {
    this.mappings = mappings;
    this.configuration = configuration;
    this.repositories = repositories;
    this.typeResolver = typeResolver;
  }

  @Override
  public List<RequestHandler> requestHandlers() {
    List<EntityContext> contexts = newArrayList();
    for (Class each : repositories) {
      RepositoryInformation repository = repositories.getRepositoryInformationFor(each);
//      CrudMethods crudMethods = repository.getCrudMethods();
//      Iterable<Method> queryMethods = repository.getQueryMethods();
      ResourceMetadata resource = mappings.getMetadataFor(each);
//      SearchResourceMappings searchResource = mappings.getSearchResourceMappings(each);
//      crudMethods.hasDelete();
      contexts.add(new EntityContext(configuration, repository, resource, typeResolver, mappings));

    }
    return FluentIterable.from(contexts)
        .transformAndConcat(toRequestHandler())
        .toList();
  }

  private Function<EntityContext, Iterable<RequestHandler>> toRequestHandler() {
    return new Function<EntityContext, Iterable<RequestHandler>>() {
      @Override
      public Iterable<RequestHandler> apply(EntityContext input) {
        return input.requestHandlers();
      }
    };
  }
}
