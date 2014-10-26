package com.mangofactory.swagger.devapp.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

  @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
  public void getUser(@PathVariable String id) {

  }

  @RequestMapping(value = "/users", method = RequestMethod.GET)
  public void allUsers() {

  }
}
