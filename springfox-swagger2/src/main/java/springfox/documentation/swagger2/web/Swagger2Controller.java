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

package springfox.documentation.swagger2.web;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import io.swagger.models.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponents;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.output.MultiFormatSerializer;
import springfox.documentation.spring.web.output.RawOutput;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import static springfox.documentation.swagger2.web.HostNameProvider.componentsFrom;

@Controller
@ApiIgnore
public class Swagger2Controller {

  public static final String DEFAULT_URL = "/v2/api-docs";

  @Value("${springfox.documentation.swagger.v2.host:DEFAULT}")
  private String hostNameOverride;

  @Autowired
  private DocumentationCache documentationCache;

  @Autowired
  private ServiceModelToSwagger2Mapper mapper;

  @Autowired
  private MultiFormatSerializer multiFormatSerializer;

  @ApiIgnore
  @RequestMapping(value = "${springfox.documentation.swagger.v2.path:" + DEFAULT_URL + "}",
      method = RequestMethod.GET)
  public
  @ResponseBody
  ResponseEntity<RawOutput> getDocumentation(
      @RequestHeader HttpHeaders headers,
      @RequestParam(value = "group", required = false) String swaggerGroup,
      HttpServletRequest servletRequest) {

    String groupName = Optional.fromNullable(swaggerGroup).or(Docket.DEFAULT_GROUP_NAME);
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<RawOutput>(HttpStatus.NOT_FOUND);
    }
    Swagger swagger = mapper.mapDocumentation(documentation);
    if (isNullOrEmpty(swagger.getHost())) {
      final UriComponents uriComponents = componentsFrom(servletRequest);
      swagger.basePath(Strings.isNullOrEmpty(uriComponents.getPath()) ? "/" : uriComponents.getPath());
      swagger.host(hostName(uriComponents));
    }
    return respond(headers.getAccept(), swagger);
  }

  private ResponseEntity<RawOutput> respond(List<MediaType> acceptableFormats, Swagger swagger) {
    MediaType availableMediaType = multiFormatSerializer.findAvailableMediaType(acceptableFormats);
    if (null == availableMediaType) {
      return new ResponseEntity<RawOutput>(UNSUPPORTED_MEDIA_TYPE);
    }
    return ResponseEntity.ok()
            .contentType(availableMediaType)
            .body(multiFormatSerializer.serialize(swagger, availableMediaType));
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
