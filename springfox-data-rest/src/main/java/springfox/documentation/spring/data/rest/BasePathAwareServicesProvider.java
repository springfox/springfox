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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.BasePathAwareHandlerMapping;
import org.springframework.data.rest.webmvc.alps.AlpsController;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static springfox.documentation.spring.web.paths.Paths.*;

@Component
public class BasePathAwareServicesProvider implements RequestHandlerProvider {
  private final BasePathAwareHandlerMapping basePathAwareMappings;
  private final HandlerMethodResolver methodResolver;
  private final String contextPath;

  @Autowired
  public BasePathAwareServicesProvider(
      RepositoryRestConfiguration repositoryConfiguration,
      ApplicationContext applicationContext,
      HandlerMethodResolver methodResolver,
      ServletContext servletContext) {
    basePathAwareMappings = new BasePathAwareHandlerMapping(repositoryConfiguration);
    this.methodResolver = methodResolver;
    basePathAwareMappings.setApplicationContext(applicationContext);
    basePathAwareMappings.afterPropertiesSet();
    contextPath = contextPath(servletContext.getContextPath());
  }

  @SuppressWarnings("java:S1872")
  private static boolean isEntitySchemaService(HandlerMethod input) {
    //For ensuring this bean is in the path (version conflict)
    return input.getBeanType().getSimpleName().equals("RepositorySchemaController");
  }

  private static boolean isAlpsProfileServices(HandlerMethod input) {
    return AlpsController.class.equals(input.getBeanType());
  }

  @Override
  public List<RequestHandler> requestHandlers() {
    List<RequestHandler> requestHandlers = new ArrayList<>();
    for (Map.Entry<RequestMappingInfo, HandlerMethod> each : basePathAwareMappings.getHandlerMethods().entrySet()) {
      if (!isEntitySchemaService(each.getValue())
          && !isAlpsProfileServices(each.getValue())) {
        requestHandlers.add(
            new WebMvcRequestHandler(
                contextPath,
                methodResolver,
                each.getKey(),
                each.getValue()));
      }
    }
    return requestHandlers;
  }
}
