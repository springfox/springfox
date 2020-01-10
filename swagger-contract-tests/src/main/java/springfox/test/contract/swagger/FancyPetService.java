/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.test.contract.swagger;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.test.contract.swagger.models.FancyPet;
import springfox.test.contract.swagger.models.Pet;

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
  public int createObject(
      @RequestBody FancyPet object,
      @PathVariable int someId) {
    int id = super.createObject(object);
    // do some logic with sub class
    return id;
  }
}
