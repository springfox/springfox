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

import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.PropertySourcedMapping;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import java.util.Optional;

import static org.springframework.util.MimeTypeUtils.*;
import static org.springframework.util.StringUtils.*;

@Controller
@ConditionalOnClass(name = "org.springframework.web.reactive.BindingContext")
@ApiIgnore
public class Swagger2ControllerWebFlux {

  private static final String DEFAULT_URL = "/v2/api-docs";
  private static final Logger LOGGER = LoggerFactory.getLogger(Swagger2ControllerWebFlux.class);
  private static final String HAL_MEDIA_TYPE = "application/hal+json";

  private final DocumentationCache documentationCache;
  private final ServiceModelToSwagger2Mapper mapper;
  private final JsonSerializer jsonSerializer;

  @Autowired
  public Swagger2ControllerWebFlux(
      DocumentationCache documentationCache,
      ServiceModelToSwagger2Mapper mapper,
      JsonSerializer jsonSerializer) {
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
      ServerHttpRequest request) {

    String groupName = Optional.ofNullable(swaggerGroup).orElse(Docket.DEFAULT_GROUP_NAME);
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      LOGGER.warn("Unable to find specification for group {}", groupName);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Swagger swagger = mapper.mapDocumentation(documentation);
    swagger.basePath(isEmpty(request.getPath().contextPath().value()) ? "/" : request.getPath().contextPath().value());
    if (isEmpty(swagger.getHost())) {
      swagger.host(request.getURI().getAuthority());
    }
    return new ResponseEntity<>(jsonSerializer.toJson(swagger), HttpStatus.OK);
  }
}
