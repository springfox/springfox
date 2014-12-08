package com.mangofactory.swagger.dummy.controllers;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.dummy.models.Pet;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@ApiIgnore
@RequestMapping("/excluded")
@Api(value = "", description = "Operations that are excluded")
public class ExcludedService {
  @RequestMapping(method = RequestMethod.POST)
  public void someExcludedOperation(
          @ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
    throw new RuntimeException("NotImplementedException");
  }

  @RequestMapping(value = "/another", method = RequestMethod.POST)
  public void anotherExcludedOperation(
          @ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
    throw new RuntimeException("NotImplementedException");
  }
}
