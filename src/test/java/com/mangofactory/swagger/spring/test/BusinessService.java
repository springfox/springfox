package com.mangofactory.swagger.spring.test;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@Api(value="", description="Business entity services")
public class BusinessService {

  @RequestMapping(value="/businesses/{businessId}",method= RequestMethod.GET)
  @ApiOperation(value = "Find a business by its id")
  public String getAllBusinesses (
          @ApiParam(value = "ID of business", required = true) @PathVariable("businessId") String businessId) {
    return "This is only a test";
  }
}