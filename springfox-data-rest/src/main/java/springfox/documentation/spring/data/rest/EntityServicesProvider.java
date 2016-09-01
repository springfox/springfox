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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.BasePathAwareHandlerMapping;
import org.springframework.data.rest.webmvc.RepositoryRestHandlerMapping;
import org.springframework.data.rest.webmvc.support.JpaHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.WebMvcRequestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;

@Component
public class EntityServicesProvider implements RequestHandlerProvider {
  private final ResourceMappings mappings;
  private final RepositoryRestConfiguration repositoryConfiguration;
  private final RepositoryRestHandlerMapping restMappings;
  private final BasePathAwareHandlerMapping basePathAwareMappings;

  @Autowired
  public EntityServicesProvider(
      ResourceMappings mappings,
      JpaHelper jpaHelper,
      RepositoryRestConfiguration repositoryConfiguration,
      ApplicationContext applicationContext) {
    this.mappings = mappings;
    this.repositoryConfiguration = repositoryConfiguration;
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
    for (Map.Entry<RequestMappingInfo, HandlerMethod> each: restMappings.getHandlerMethods().entrySet()) {
      requestHandlers.add(new WebMvcRequestHandler(each.getKey(), each.getValue()));
    }
    for (Map.Entry<RequestMappingInfo, HandlerMethod> each: basePathAwareMappings.getHandlerMethods().entrySet()) {
      requestHandlers.add(new WebMvcRequestHandler(each.getKey(), each.getValue()));
    }
    return requestHandlers;
  }
}
