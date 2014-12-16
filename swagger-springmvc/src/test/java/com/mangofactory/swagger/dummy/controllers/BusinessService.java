package com.mangofactory.swagger.dummy.controllers;

import com.mangofactory.swagger.dummy.models.Business;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.google.common.collect.Lists.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@Controller
@Api(value = "", description = "Services to demonstrate path variable resolution")
public class BusinessService {

  @RequestMapping(value = "/businesses/aliased/{otherId}", method = RequestMethod.GET)
  @ApiOperation(value = "Find a business by its id")
  public String getAliasedPathVariable(
          @ApiParam(value = "ID of business", required = true) @PathVariable("otherId") String businessId) {
    return "This is only a test";
  }

  @RequestMapping(value = "/businesses/non-aliased/{businessId}", method = RequestMethod.GET)
  @ApiOperation(value = "Find a business by its id")
  public String getNonAliasedPathVariable(
          @ApiParam(value = "ID of business", required = true) @PathVariable("businessId") String businessId) {
    return "This is only a test";
  }

  @RequestMapping(value = "/businesses/vanilla/{businessId}", method = RequestMethod.GET)
  public String getVanillaPathVariable(@PathVariable String businessId) {
    return "This is only a test";
  }

  @RequestMapping(value = "/businesses/responseEntity/{businessId}", method = RequestMethod.GET)
  public ResponseEntity<String> getResponseEntity(@PathVariable String businessId) {
    return new ResponseEntity<String>("This is only a test", HttpStatus.OK);
  }

  @RequestMapping(value = {"/businesses/byTypes"}, method = GET, produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public List<Business> businessesByCategories(@RequestParam Business.BusinessType[] types) {
    return newArrayList();
  }
}