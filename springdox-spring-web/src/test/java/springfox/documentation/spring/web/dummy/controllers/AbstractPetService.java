package springfox.documentation.spring.web.dummy.controllers;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.spring.web.dummy.models.Pet;

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

