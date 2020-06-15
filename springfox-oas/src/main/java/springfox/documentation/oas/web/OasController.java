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

package springfox.documentation.oas.web;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ServerBuilder;
import springfox.documentation.oas.mappers.ServiceModelToOasMapper;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.Server;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.PropertySourcedMapping;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.util.MimeTypeUtils.*;

@Controller
@ApiIgnore
public class OasController {

  private static final String DEFAULT_URL = "/v3/api-docs";
  private static final String HAL_MEDIA_TYPE = "application/hal+json";

  private final DocumentationCache documentationCache;
  private final ServiceModelToOasMapper mapper;
  private final JsonSerializer jsonSerializer;
  private final String oasPath;

  @Autowired
  public OasController(
      DocumentationCache documentationCache,
      ServiceModelToOasMapper mapper,
      JsonSerializer jsonSerializer,
      @Value("${springfox.documentation.oas.path:/v3/api-docs}") String oasPath) {

    this.documentationCache = documentationCache;
    this.mapper = mapper;
    this.jsonSerializer = jsonSerializer;
    this.oasPath = oasPath;
  }

  @RequestMapping(
      value = DEFAULT_URL,
      method = RequestMethod.GET,
      produces = {
          APPLICATION_JSON_VALUE,
          HAL_MEDIA_TYPE })
  @PropertySourcedMapping(
      value = "${springfox.documentation.oas.path}",
      propertyKey = "springfox.documentation.oas.path")
  @ResponseBody
  public ResponseEntity<Json> getDocumentation(
      @RequestParam(value = "group", required = false) String swaggerGroup,
      HttpServletRequest servletRequest) {

    String groupName = Optional.ofNullable(swaggerGroup).orElse(Docket.DEFAULT_GROUP_NAME);
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    documentation.addServer(inferredServer(servletRequest, oasPath));
    OpenAPI oas = mapper.mapDocumentation(documentation);
    return new ResponseEntity<>(
        jsonSerializer.toJson(oas),
        HttpStatus.OK);
  }

  private Server inferredServer(
      HttpServletRequest serverHttpRequest,
      String apiDocsUrl) {
    String requestUrl = decode(serverHttpRequest.getRequestURL().toString());
    return new ServerBuilder()
        .url(requestUrl.substring(0, requestUrl.length() - apiDocsUrl.length()))
        .description("Inferred Url")
        .build();
  }

  protected String decode(String requestURI) {
    try {
      return URLDecoder.decode(requestURI, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      return requestURI;
    }
  }
}
