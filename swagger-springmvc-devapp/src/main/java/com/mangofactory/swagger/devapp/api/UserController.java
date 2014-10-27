package com.mangofactory.swagger.devapp.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

  @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
  public void getUser(@PathVariable String id) {

  }

  @RequestMapping(value = "/users/search", method = RequestMethod.GET)
  public void search(@RequestParam String name, @RequestParam int age) {

  }

  @RequestMapping(value = "/users", method = RequestMethod.GET)
  public void allUsers() {

  }
}
