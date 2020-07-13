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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.oas.mappers.ServiceModelToOpenApiMapper;
import springfox.documentation.service.Documentation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.OnServletBasedWebApplication;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.MimeTypeUtils.*;
import static springfox.documentation.oas.web.SpecGeneration.*;

@ApiIgnore
@RestController
@RequestMapping(OPEN_API_SPECIFICATION_PATH)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Conditional(OnServletBasedWebApplication.class)
public class OpenApiControllerWebMvc {

  private final DocumentationCache documentationCache;
  private final ServiceModelToOpenApiMapper mapper;
  private final JsonSerializer jsonSerializer;
  private final PluginRegistry<WebMvcOpenApiTransformationFilter, DocumentationType> transformations;

  @Autowired
  public OpenApiControllerWebMvc(
      DocumentationCache documentationCache,
      ServiceModelToOpenApiMapper mapper,
      JsonSerializer jsonSerializer,
      @Qualifier("webMvcOpenApiTransformationFilterRegistry")
          PluginRegistry<WebMvcOpenApiTransformationFilter, DocumentationType> transformations) {
    this.documentationCache = documentationCache;
    this.mapper = mapper;
    this.jsonSerializer = jsonSerializer;
    this.transformations = transformations;
  }

  @GetMapping(
      produces = {
          APPLICATION_JSON_VALUE,
          HAL_MEDIA_TYPE})
  public ResponseEntity<Json> getDocumentation(
      @RequestParam(value = "group", required = false) String group,
      HttpServletRequest servletRequest) {
    String groupName = Optional.ofNullable(group).orElse(Docket.DEFAULT_GROUP_NAME);
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    OpenAPI oas = mapper.mapDocumentation(documentation);
    OpenApiTransformationContext<HttpServletRequest> context
        = new OpenApiTransformationContext<>(oas, servletRequest);
    List<WebMvcOpenApiTransformationFilter> filters = transformations.getPluginsFor(DocumentationType.OAS_30);
    for (WebMvcOpenApiTransformationFilter each : filters) {
      context = context.next(each.transform(context));
    }
    return new ResponseEntity<>(jsonSerializer.toJson(oas), HttpStatus.OK);
  }
}
