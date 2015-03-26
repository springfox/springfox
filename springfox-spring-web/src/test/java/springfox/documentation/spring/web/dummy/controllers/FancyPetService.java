package springfox.documentation.spring.web.dummy.controllers;

import com.wordnik.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.spring.web.dummy.models.FancyPet;
import springfox.documentation.spring.web.dummy.models.Pet;

@Controller
@RequestMapping("/fancypets")
@Api(value = "Fancy Pet Service", description = "Operations about fancy pets")
public class FancyPetService extends AbstractPetService<FancyPet> {

  // some subclass dependency here
  // override one of superclass
  @Override
  @ResponseBody
  public int createObject(@RequestBody FancyPet object) {
    int id = super.createObject(object);
    // do some logic with sub class
    return id;
  }


  //Example of generic type constraint
  @RequestMapping(method = RequestMethod.PUT)
  public <T extends Pet> void updatePet(@RequestBody T pet) {
    throw new UnsupportedOperationException();
  }

  // overload one of superclass
  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "?{someId}")
  public int createObject(@RequestBody FancyPet object, @PathVariable int someId) {
    int id = super.createObject(object);
    // do some logic with sub class
    return id;
  }
}
