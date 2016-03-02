/*
 *
 *  Copyright 2015 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.stereotype.Component;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class EntityServicesProvider implements RequestHandlerProvider {
  private final ResourceMappings mappings;
  private final RepositoryConfiguration repositoryConfiguration;

  @Autowired
  public EntityServicesProvider(ResourceMappings mappings, RepositoryConfiguration repositoryConfiguration) {
    this.mappings = mappings;
    this.repositoryConfiguration = repositoryConfiguration;
  }


  @Override
  public List<RequestHandler> requestHandlers() {
    for (ResourceMetadata each : mappings) {
    }
    return newArrayList();
  }
}
