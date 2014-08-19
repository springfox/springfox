package com.mangofactory.swagger.dummy.controllers;

import com.mangofactory.swagger.dummy.models.Pet;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public abstract class AbstractPetService<T extends Pet> {

  // some dependency here
  // some crud here
  // e.g.
  @RequestMapping(method = RequestMethod.POST)
  @ResponseBody
  public int createObject(@RequestBody T object) {
    // do some logic here
    return 1;
  }
}

