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

package springfox.documentation.swagger2.web;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponents;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.PropertySourcedMapping;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.Strings.*;
import static org.springframework.util.MimeTypeUtils.*;
import static springfox.documentation.swagger2.web.HostNameProvider.*;

@Controller
@ApiIgnore
public class Swagger2Controller {

  public static final String DEFAULT_URL = "/v2/api-docs";
  private static final Logger LOGGER = LoggerFactory.getLogger(Swagger2Controller.class);
  private static final String HAL_MEDIA_TYPE = "application/hal+json";

  private final String hostNameOverride;
  private final DocumentationCache documentationCache;
  private final ServiceModelToSwagger2Mapper mapper;
  private final JsonSerializer jsonSerializer;

  @Autowired
  public Swagger2Controller(
      Environment environment,
      DocumentationCache documentationCache,
      ServiceModelToSwagger2Mapper mapper,
      JsonSerializer jsonSerializer) {

    this.hostNameOverride =
        environment.getProperty(
            "springfox.documentation.swagger.v2.host",
            "DEFAULT");
    this.documentationCache = documentationCache;
    this.mapper = mapper;
    this.jsonSerializer = jsonSerializer;
  }

  @RequestMapping(
      value = DEFAULT_URL,
      method = RequestMethod.GET,
      produces = { APPLICATION_JSON_VALUE, HAL_MEDIA_TYPE })
  @PropertySourcedMapping(
      value = "${springfox.documentation.swagger.v2.path}",
      propertyKey = "springfox.documentation.swagger.v2.path")
  @ResponseBody
  public ResponseEntity<Json> getDocumentation(
      @RequestParam(value = "group", required = false) String swaggerGroup,
      HttpServletRequest servletRequest) {

    String groupName = Optional.fromNullable(swaggerGroup).or(Docket.DEFAULT_GROUP_NAME);
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      LOGGER.warn("Unable to find specification for group {}", groupName);
      return new ResponseEntity<Json>(HttpStatus.NOT_FOUND);
    }
    Swagger swagger = mapper.mapDocumentation(documentation);
    UriComponents uriComponents = componentsFrom(servletRequest, swagger.getBasePath());
    swagger.basePath(Strings.isNullOrEmpty(uriComponents.getPath()) ? "/" : uriComponents.getPath());
    if (isNullOrEmpty(swagger.getHost())) {
      swagger.host(hostName(uriComponents));
    }
    return new ResponseEntity<Json>(jsonSerializer.toJson(swagger), HttpStatus.OK);
  }

  private String hostName(UriComponents uriComponents) {
    if ("DEFAULT".equals(hostNameOverride)) {
      String host = uriComponents.getHost();
      int port = uriComponents.getPort();
      if (port > -1) {
        return String.format("%s:%d", host, port);
      }
      return host;
    }
    return hostNameOverride;
  }
}
