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

package springfox.documentation.staticdocs.web;

import com.google.common.base.Optional;
import com.wordnik.swagger.models.Swagger;
import io.github.robwin.swagger2markup.Swagger2MarkupConverter;
import org.asciidoctor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import java.io.IOException;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@ApiIgnore
public class StaticDocsController {

    private static final Logger LOG = LoggerFactory.getLogger(StaticDocsController.class);

  public static final String DEFAULT_URL = "/static-docs";
    public static final String DOC_TYPE = "book";
    public static final String HTML5 = "html5";
    public static final String SECTLINKS = "sectlinks";

    @Autowired
  private DocumentationCache documentationCache;

  @Autowired
  private ServiceModelToSwagger2Mapper mapper;

  @ApiIgnore
  @RequestMapping(value = "${springfox.documentation.static.path:" + DEFAULT_URL + "}",
      method = RequestMethod.GET,
      produces = MediaType.TEXT_HTML_VALUE
  )
  public @ResponseBody ResponseEntity<String> getDocumentation(
          @RequestParam(value = "group", required = false) String swaggerGroup) {

    String groupName = Optional.fromNullable(swaggerGroup).or("default");
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }
    Swagger swagger = mapper.mapDocumentation(documentation);
    URI uri = linkTo(StaticDocsController.class).toUri();
    swagger.host(String.format("%s:%s", uri.getHost(), uri.getPort()));
    try {
        String asciiDoc = Swagger2MarkupConverter.from(swagger).build().asString();
        String asciiDocAsHtml = Asciidoctor.Factory.create().convert(asciiDoc,
                OptionsBuilder.options().backend(HTML5).headerFooter(true).safe(SafeMode.UNSAFE).docType(DOC_TYPE)
                        .attributes(AttributesBuilder.attributes()
                                .tableOfContents(true)
                                .tableOfContents(Placement.LEFT)
                                .sectionNumbers(true)
                                .hardbreaks(true)
                                .setAnchors(true)
                                .attribute(SECTLINKS)));
      return new ResponseEntity<String>(asciiDocAsHtml, HttpStatus.OK);
    } catch (IOException e) {
      LOG.error("Failed to convert Swagger into HTML", e);
      return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
