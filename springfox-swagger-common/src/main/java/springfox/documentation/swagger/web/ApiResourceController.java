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
package springfox.documentation.swagger.web;

import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@ApiIgnore
public class ApiResourceController {

  @Value("${springfox.documentation.swagger.v1.path:/api-docs}")
  private String swagger1Url;

  @Value("${springfox.documentation.swagger.v2.path:/v2/api-docs}")
  private String swagger2Url;

  @Autowired
  private DocumentationCache documentationCache;

  @Autowired(required = false)
  private SecurityConfiguration securityConfiguration;
  @Autowired(required = false)
  private UiConfiguration uiConfiguration;

  @RequestMapping(value = "/configuration/security")
  @ResponseBody
  ResponseEntity<SecurityConfiguration> securityConfiguration() {
    return new ResponseEntity<SecurityConfiguration>(
        Optional.fromNullable(securityConfiguration).or(SecurityConfiguration.DEFAULT), HttpStatus.OK);
  }

  @RequestMapping(value = "/configuration/ui")
  @ResponseBody
  ResponseEntity<UiConfiguration> uiConfiguration() {
    return new ResponseEntity<UiConfiguration>(
        Optional.fromNullable(uiConfiguration).or(UiConfiguration.DEFAULT), HttpStatus.OK);
  }

  @RequestMapping(value = "/swagger-resources")
  @ResponseBody
  ResponseEntity<List<SwaggerResource>> swaggerResources() {
    Class swagger1Controller = classOrNull("springfox.documentation.swagger1.web.Swagger1Controller");
    Class swagger2Controller = classOrNull("springfox.documentation.swagger2.web.Swagger2Controller");

    List<SwaggerResource> resources = new ArrayList<SwaggerResource>();

    for (Map.Entry<String, Documentation> entry : documentationCache.all().entrySet()) {
      String swaggerGroup = entry.getKey();
      if (swagger1Controller != null) {
        SwaggerResource swaggerResource = resource(swaggerGroup, swagger1Url);
        swaggerResource.setSwaggerVersion("1.2");
        resources.add(swaggerResource);
      }

      if (swagger2Controller != null) {
        SwaggerResource swaggerResource = resource(swaggerGroup, swagger2Url);
        swaggerResource.setSwaggerVersion("2.0");
        resources.add(swaggerResource);
      }
    }
    Collections.sort(resources);
    return new ResponseEntity<List<SwaggerResource>>(resources, HttpStatus.OK);
  }

  private SwaggerResource resource(String swaggerGroup, String baseUrl) {
    SwaggerResource swaggerResource = new SwaggerResource();
    swaggerResource.setName(swaggerGroup);
    swaggerResource.setLocation(swaggerLocation(baseUrl, swaggerGroup));
    return swaggerResource;
  }

  private String swaggerLocation(String swaggerUrl, String swaggerGroup) {
    String base = Optional.of(swaggerUrl).get();
    if (Docket.DEFAULT_GROUP_NAME.equals(swaggerGroup)) {
      return base;
    }
    return base + "?group=" + swaggerGroup;
  }

  private Class classOrNull(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }
}
