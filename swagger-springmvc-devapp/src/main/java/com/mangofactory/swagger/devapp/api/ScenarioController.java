package com.mangofactory.swagger.devapp.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScenarioController {
  @RequestMapping(value = "/foo/{businessId:\\w+}", method = RequestMethod.GET)
  public String patternWithRegex(@PathVariable String businessId) {
    return businessId;
  }
}
