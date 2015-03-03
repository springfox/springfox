package com.mangofactory.documentation.swagger2.web;

import com.google.common.base.Optional;
import com.mangofactory.documentation.annotations.ApiIgnore;
import com.mangofactory.documentation.service.Documentation;
import com.mangofactory.documentation.spring.web.GroupCache;
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

@Controller
public class Swagger2Controller {
  public static final String DOCUMENTATION_BASE_PATH = "/v2/api-docs";

  @Autowired
  private GroupCache groupCache;

  @Autowired
  private ServiceModelToSwagger2Mapper mapper;

  @ApiIgnore
  @RequestMapping(value = {DOCUMENTATION_BASE_PATH}, method = RequestMethod.GET)
  public
  @ResponseBody
  ResponseEntity<Swagger> getDocumentation(
          @RequestParam(value = "group", required = false) String swaggerGroup) {

    String groupName = Optional.fromNullable(swaggerGroup).or("default");
    Documentation documentation = groupCache.getGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<Swagger>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<Swagger>(mapper.map(documentation), HttpStatus.OK);
  }

}
