package com.mangofactory.swagger.devapp.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
  public void getUser(@PathVariable String id) {

  }

  @RequestMapping(value = "/users/search", method = RequestMethod.GET)
  public void search(@RequestParam String name, @RequestParam int age) {

  }

  @RequestMapping(value = "/users", method = RequestMethod.GET)
  @ResponseBody
  public List<User> allUsers() {
    return new ArrayList<User>();
  }

  @RequestMapping(value = "/users/advancedSearch", method = RequestMethod.GET)
  public
  @ResponseBody
  List<User> advancedSearch(@ModelAttribute UserSearch userSearch) {
    log.info("Search criteria: {}", userSearch);
    List<User> users = new ArrayList<User>();
    users.add(new User("id-123", "name-123"));
    return users;
  }
}
