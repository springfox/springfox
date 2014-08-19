package com.mangofactory.swagger.dummy;

import com.wordnik.swagger.annotations.Api;
import org.springframework.stereotype.Controller;

@Controller
@Api(value = "Group name", description = "Dummy Controller Description", position = 2)
public class DummyControllerWithApiDescription {
  public void dummyMethod() {

  }
}