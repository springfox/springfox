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
      @RequestParam(value = "group",  required = false) String swaggerGroup) {

    return getSwaggerResourceListing(swaggerGroup);
  }

//  @ApiIgnore
//  @RequestMapping(value = {DOCUMENTATION_BASE_PATH + "/{swaggerGroup}/{apiDeclaration}"}, method = RequestMethod.GET)
//  public
//  @ResponseBody
//  ResponseEntity<ApiListing> getApiListing(@PathVariable String swaggerGroup, @PathVariable String apiDeclaration) {
//    return getSwaggerApiListing(swaggerGroup, apiDeclaration);
//  }

//  private ResponseEntity<ApiListing> getSwaggerApiListing(String swaggerGroup, String apiDeclaration) {
//    ResponseEntity<ApiListing> responseEntity = new ResponseEntity<ApiListing>(HttpStatus.NOT_FOUND);
//    Map<String, ApiListing> apiListingMap = swaggerCache.getSwaggerApiListingMap().get(swaggerGroup);
//    if (null != apiListingMap) {
//      ApiListing apiListing = apiListingMap.get(apiDeclaration);
//      if (null != apiListing) {
//        responseEntity = new ResponseEntity<ApiListing>(apiListing, HttpStatus.OK);
//      }
//    }
//    return responseEntity;
//  }

  private ResponseEntity<Swagger> getSwaggerResourceListing(String swaggerGroup) {
//    ResponseEntity<ResourceListing> responseEntity = new ResponseEntity<ResourceListing>(HttpStatus.NOT_FOUND);
//    ResourceListing resourceListing = null;
//
//    if (null == swaggerGroup) {
//      resourceListing = swaggerCache.getSwaggerMap().values().iterator().next();
//    } else {
//      if (swaggerCache.getSwaggerMap().containsKey(swaggerGroup)) {
//        resourceListing = swaggerCache.getSwaggerMap().get(swaggerGroup);
//      }
//    }
//    if (null != resourceListing) {
//      responseEntity = new ResponseEntity<ResourceListing>(resourceListing, HttpStatus.OK);
//    }

    Swagger swagger = new Swagger();

    return new ResponseEntity<Swagger>(swagger, HttpStatus.OK);
  }
}
