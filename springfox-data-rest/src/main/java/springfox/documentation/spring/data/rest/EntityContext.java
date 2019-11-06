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
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.rest.core.Path;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.webmvc.mapping.Associations;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

import static springfox.documentation.spring.web.paths.Paths.*;

public class EntityContext {
  private final RepositoryRestConfiguration configuration;
  private final RepositoryInformation repository;
  private final Object repositoryInstance;
  private final ResourceMetadata resource;
  private final TypeResolver typeResolver;
  private final ResourceMappings mappings;
  private final PersistentEntities entities;
  private final Associations associations;
  private final RequestHandlerExtractorConfiguration extractorConfiguration;
  private final String contextPath;
  
  @SuppressWarnings("ParameterNumber")
  public EntityContext(
      TypeResolver typeResolver,
      String contextPath,
      RepositoryRestConfiguration configuration,
      RepositoryInformation repository,
      Object repositoryInstance,
      ResourceMetadata resource,
      ResourceMappings mappings,
      PersistentEntities entities,
      Associations associations,
      RequestHandlerExtractorConfiguration extractorConfiguration) {

    this.configuration = configuration;
    this.repository = repository;
    this.repositoryInstance = repositoryInstance;
    this.resource = resource;
    this.typeResolver = typeResolver;
    this.mappings = mappings;
    this.entities = entities;
    this.associations = associations;
    this.extractorConfiguration = extractorConfiguration;
    this.contextPath = contextPath;
  }

  public String getName() {
    return resource.getDomainType().getSimpleName();
  }

  public Optional<PersistentEntity<?, ? extends PersistentProperty<?>>> entity() {
    return entities.getPersistentEntity(resource.getDomainType());
  }

  public CrudMethods crudMethods() {
    return repository.getCrudMethods();
  }

  public Object getRepositoryInstance() {
    return repositoryInstance;
  }

  public URI basePath() {
    return configuration.getBasePath();
  }

  public Path resourcePath() {
    return resource.getPath();
  }

  public TypeResolver getTypeResolver() {
    return typeResolver;
  }

  public RepositoryMetadata getRepositoryMetadata() {
    return repository;
  }

  public RepositoryRestConfiguration getConfiguration() {
    return configuration;
  }

  public SearchResourceMappings searchMappings() {
    return mappings.getSearchResourceMappings(repository.getDomainType());
  }

  public Associations getAssociations() {
    return associations;
  }

  public Collection<EntityAssociationOperationsExtractor> getAssociationExtractors() {
    return extractorConfiguration.getAssociationExtractors();
  }

  public String contextPath() {
    return rootPathWhenEmpty(contextPath);
  }
}
