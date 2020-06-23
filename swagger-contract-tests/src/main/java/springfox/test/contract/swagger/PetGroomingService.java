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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
//Demonstrates multiple request mappings at the controller level
@RequestMapping({ "/petgrooming", "/pets/grooming", "/pets" })
@Api(value = "", description = "Grooming operations for pets")
public class PetGroomingService {

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<Boolean> canGroom(@RequestParam String type) {
    return new ResponseEntity<Boolean>(HttpStatus.OK);
  }

  //void returns
  @RequestMapping(value = "voidMethod/{input}", method = RequestMethod.DELETE,
                  headers = { "Accept=application/xml,application/json" })
  @ResponseStatus(value = HttpStatus.OK, reason = "Just testing")
  public void groomingFunctionThatReturnsVoid(@PathVariable("input") String input) throws Exception {
  }

}
