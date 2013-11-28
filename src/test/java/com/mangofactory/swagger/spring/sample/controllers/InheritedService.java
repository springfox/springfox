package com.mangofactory.swagger.spring.sample.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("child")
public interface InheritedService {

  @RequestMapping(value = "child-method", method = RequestMethod.GET)
  public String getSomething (String parameter);
  
}