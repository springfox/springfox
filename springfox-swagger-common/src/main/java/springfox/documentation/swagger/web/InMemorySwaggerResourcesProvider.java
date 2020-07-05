/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.service.Documentation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Optional.*;
import static springfox.documentation.schema.ClassSupport.*;

@Component
public class InMemorySwaggerResourcesProvider implements SwaggerResourcesProvider, ApplicationContextAware {
  private final String swagger1Url;
  private final String swagger2Url;
  private final String oas3Url;
  
  private boolean oas3Available;
  private boolean swagger1Available;
  private boolean swagger2Available;

  private final DocumentationCache documentationCache;
  private final boolean oas3DocketsPresent;
  private final boolean swagger2DocketsPresent;

  @Autowired
  public InMemorySwaggerResourcesProvider(
      Environment environment,
      DocumentationCache documentationCache,
      DocumentationPluginsManager pluginsManager) {
    oas3DocketsPresent = pluginsManager.documentationPlugins().stream()
        .anyMatch(d -> d.supports(DocumentationType.OAS_30));
    swagger2DocketsPresent = pluginsManager.documentationPlugins().stream()
        .anyMatch(d -> d.supports(DocumentationType.SWAGGER_2));
    swagger1Url = environment.getProperty("springfox.documentation.swagger.v1.path", "/api-docs");
    swagger2Url = fixup(environment.getProperty(
        "springfox.documentation.swagger.v2.path",
        "/v2/api-docs"));
    oas3Url = fixup(environment.getProperty(
        "springfox.documentation.open-api.v3.path",
        "/v3/api-docs"));
    this.documentationCache = documentationCache;
  }

  private String fixup(String path) {
    if (StringUtils.isEmpty(path)
        || "/".equals(path)
        || "//".equals(path)) {
      return "/";
    }
    return StringUtils.trimTrailingCharacter(path.replace("//", "/"), '/');
  }

  @Override
  public List<SwaggerResource> get() {
    List<SwaggerResource> resources = new ArrayList<>();

    for (Map.Entry<String, Documentation> entry : documentationCache.all().entrySet()) {
      String swaggerGroup = entry.getKey();
      if (swagger1Available) {
        SwaggerResource swaggerResource = resource(swaggerGroup, swagger1Url);
        swaggerResource.setSwaggerVersion("1.2");
        resources.add(swaggerResource);
      }

      if (swagger2Available) {
        SwaggerResource swaggerResource = resource(swaggerGroup, swagger2Url);
        swaggerResource.setSwaggerVersion("2.0");
        resources.add(swaggerResource);
      }

      if (oas3Available) {
        SwaggerResource swaggerResource = resource(swaggerGroup, oas3Url);
        swaggerResource.setSwaggerVersion("3.0.3");
        resources.add(swaggerResource);
      }
    }
    Collections.sort(resources);
    return resources;
  }

  private SwaggerResource resource(
      String swaggerGroup,
      String baseUrl) {
    SwaggerResource swaggerResource = new SwaggerResource();
    swaggerResource.setName(swaggerGroup);
    swaggerResource.setUrl(swaggerLocation(baseUrl, swaggerGroup));
    return swaggerResource;
  }

  private String swaggerLocation(
      String swaggerUrl,
      String swaggerGroup) {
    String base = of(swaggerUrl).get();
    if (Docket.DEFAULT_GROUP_NAME.equals(swaggerGroup)) {
      return base;
    }
    return base + "?group=" + swaggerGroup;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    ClassLoader classLoader = applicationContext.getClassLoader();
    swagger1Available
        = classByName("springfox.documentation.swagger1.web.Swagger1Controller", classLoader).isPresent();
    swagger2Available =
        (classByName("springfox.documentation.swagger2.web.Swagger2ControllerWebFlux", classLoader)
            .isPresent()
            || classByName("springfox.documentation.swagger2.web.Swagger2ControllerWebMvc", classLoader)
            .isPresent())
            && swagger2DocketsPresent;
    oas3Available = (classByName("springfox.documentation.oas.web.OpenApiControllerWebFlux", classLoader)
        .isPresent()
        || classByName("springfox.documentation.oas.web.OpenApiControllerWebMvc", classLoader).isPresent())
        && oas3DocketsPresent;
  }
}
