package com.mangofactory.documentation.swagger2.web;

import com.google.common.base.Optional;
import com.mangofactory.documentation.annotations.ApiIgnore;
import com.mangofactory.documentation.service.Documentation;
import com.mangofactory.documentation.spring.web.DocumentationCache;
import com.mangofactory.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import com.wordnik.swagger.models.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;

@Controller
public class Swagger2Controller {
  public static final String DOCUMENTATION_BASE_PATH = "/v2/api-docs";

  @Autowired
  private DocumentationCache documentationCache;

  @Autowired
  private ServiceModelToSwagger2Mapper mapper;

  @ApiIgnore
  @RequestMapping(value = {DOCUMENTATION_BASE_PATH}, method = RequestMethod.GET)
  public
  @ResponseBody
  ResponseEntity<Swagger> getDocumentation(
          @RequestParam(value = "group", required = false) String swaggerGroup, ServletRequest request) {

    String groupName = Optional.fromNullable(swaggerGroup).or("default");
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<Swagger>(HttpStatus.NOT_FOUND);
    }
    Swagger swagger = mapper.map(documentation);
    swagger.host(String.format("%s:%s", request.getServerName(), request.getServerPort()));
    return new ResponseEntity<Swagger>(swagger, HttpStatus.OK);
  }

}
