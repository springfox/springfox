package com.mangofactory.swagger.controllers;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.SwaggerCache;
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
public class DefaultSwaggerController {

  public static final String DOCUMENTATION_BASE_PATH = "/api-docs";

  @Autowired
  private SwaggerCache swaggerCache;

  @ApiIgnore
  @RequestMapping(value = {DOCUMENTATION_BASE_PATH}, method = RequestMethod.GET)
  public
  @ResponseBody
  ResponseEntity<Swagger> getSwagger(
          @RequestParam(value = "group", required = false) String swaggerGroup) {

    return getSwaggerResourceListing(swaggerGroup);
  }

  private ResponseEntity<Swagger> getSwaggerResourceListing(String swaggerGroup) {
    ResponseEntity<Swagger> responseEntity = new ResponseEntity<Swagger>(HttpStatus.NOT_FOUND);
    Swagger swagger = null;
    if (null == swaggerGroup) {
      swagger = swaggerCache.getSwaggerApiMap().values().iterator().next();
    } else {
      if (swaggerCache.getSwaggerApiMap().containsKey(swaggerGroup)) {
        swagger = swaggerCache.getSwaggerApiMap().get(swaggerGroup);
      }
    }
    if (null != swagger) {
      responseEntity = new ResponseEntity<Swagger>(swagger, HttpStatus.OK);
    }
    return responseEntity;
  }
}
