/*
 *
 *  Copyright 2016 the original author or authors.
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
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.data.rest.webmvc.BasePathAwareHandlerMapping;
import org.springframework.data.rest.webmvc.RepositoryRestHandlerMapping;
import org.springframework.data.rest.webmvc.support.JpaHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.WebMvcRequestHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Lists.*;
import static org.springframework.http.MediaType.*;
import static springfox.documentation.spring.data.rest.EntityServices.*;

@Component
public class EntityServicesProvider implements RequestHandlerProvider {
  private final ResourceMappings mappings;
  private final Repositories repositories;
  private final RepositoryRestHandlerMapping restMappings;
  private final BasePathAwareHandlerMapping basePathAwareMappings;
  private final TypeResolver typeResolver;

  @Autowired
  public EntityServicesProvider(
      ResourceMappings mappings,
      JpaHelper jpaHelper,
      RepositoryRestConfiguration repositoryConfiguration,
      ApplicationContext applicationContext,
      Repositories repositories,
      TypeResolver typeResolver) {
    this.mappings = mappings;
    this.repositories = repositories;
    this.typeResolver = typeResolver;
    this.restMappings = new RepositoryRestHandlerMapping(mappings, repositoryConfiguration);
    restMappings.setJpaHelper(jpaHelper);
    restMappings.setApplicationContext(applicationContext);
    restMappings.afterPropertiesSet();

    basePathAwareMappings = new BasePathAwareHandlerMapping(repositoryConfiguration);
    basePathAwareMappings.setApplicationContext(applicationContext);
    basePathAwareMappings.afterPropertiesSet();
  }


  @Override
  public List<RequestHandler> requestHandlers() {
    ArrayList<RequestHandler> requestHandlers = newArrayList();
    FluentIterable<Map.Entry<RequestMappingInfo, HandlerMethod>> entries = FluentIterable.from(allEntries());
    for (Map.Entry<RequestMappingInfo, HandlerMethod> each : entries.filter(not(restDataServices()))) {
      requestHandlers.add(new WebMvcRequestHandler(each.getKey(), each.getValue()));
    }
    for (Map.Entry<RequestMappingInfo, HandlerMethod> each : entries.filter(repositories())) {
      requestHandlers.add(new WebMvcRequestHandler(each.getKey(), each.getValue()));
    }
    for (Map.Entry<RequestMappingInfo, HandlerMethod> each : entries.filter(entityServices())) {
      EntityRequestHandlers entityRequestHandlers
          = new EntityRequestHandlers(typeResolver, repositories, mappings, each.getKey(), each.getValue());
      requestHandlers.addAll(entityRequestHandlers.operations());
    }
    List<RequestHandler> searchHandlers = newArrayList();
    for (Map.Entry<RequestMappingInfo, HandlerMethod> each : entries.filter(entitySearchServices())) {
      EntitySearchRequestHandlers entityRequestHandlers
          = new EntitySearchRequestHandlers(typeResolver, mappings, each.getKey(), each.getValue());
      searchHandlers.addAll(entityRequestHandlers.operations());
    }
    requestHandlers.addAll(maybeCombine(searchHandlers));
    for (Map.Entry<RequestMappingInfo, HandlerMethod> each : basePathAwareMappings.getHandlerMethods().entrySet()) {
      requestHandlers.add(new WebMvcRequestHandler(each.getKey(), each.getValue()));
    }
    return requestHandlers;
  }

  private Collection<RequestHandler> maybeCombine(List<RequestHandler> searchHandlers) {
    List<RequestHandler> combined = newArrayList();
    Iterable<RequestHandler> compacts = FluentIterable.from(searchHandlers)
        .filter(and(compactHandlers(), getHandler()));
    FluentIterable<RequestHandler> nonCompacts = FluentIterable.from(searchHandlers)
        .filter(and(not(compactHandlers()), getHandler()));
    for (RequestHandler compact: compacts) {
      Optional<RequestHandler> found = nonCompacts.firstMatch(samePathMapping(compact.getPatternsCondition()));
      combined.add(combine(compact, found));
    }
    combined.addAll(FluentIterable.from(searchHandlers)
        .filter(EntitySearchRequestHandler.class)
        .filter(collectionHandlers()).toList());
    return combined;
  }

  private Predicate<EntitySearchRequestHandler> collectionHandlers() {
    return new Predicate<EntitySearchRequestHandler>() {
      @Override
      public boolean apply(EntitySearchRequestHandler input) {
        return input.resourceType() == ResourceType.COLLECTION;
      }
    };
  }

  private RequestHandler combine(RequestHandler source, Optional<RequestHandler> other) {
    if (other.isPresent()) {
      return other.get().combine(source);
    }
    return source;
  }

  private Predicate<RequestHandler> samePathMapping(final PatternsRequestCondition pathMappings) {
    return new Predicate<RequestHandler>() {
      @Override
      public boolean apply(RequestHandler input) {
        return input.getPatternsCondition().equals(pathMappings);
      }
    };
  }

  private Predicate<RequestHandler> compactHandlers() {
    return new Predicate<RequestHandler>() {
      @Override
      public boolean apply(RequestHandler input) {
        return input.key().getProducibleMediaTypes().contains(valueOf("application/x-spring-data-compact+json"));
      }
    };
  }

  private Predicate<RequestHandler> getHandler() {
    return new Predicate<RequestHandler>() {
      @Override
      public boolean apply(RequestHandler input) {
        return input.key().getSupportedMethods().contains(RequestMethod.GET);
      }
    };
  }

  private Set<Map.Entry<RequestMappingInfo, HandlerMethod>> allEntries() {
    return restMappings.getHandlerMethods().entrySet();
  }
}
