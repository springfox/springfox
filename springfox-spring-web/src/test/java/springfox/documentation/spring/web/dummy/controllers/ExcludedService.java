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

package springfox.documentation.spring.web.dummy.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.dummy.models.Pet;

@Controller
@ApiIgnore
@RequestMapping("/excluded")
@Api(value = "", description = "Operations that are excluded")
public class ExcludedService {
  @RequestMapping(method = RequestMethod.POST)
  public void someExcludedOperation(
          @ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
    throw new RuntimeException("NotImplementedException");
  }

  @RequestMapping(value = "/another", method = RequestMethod.POST)
  public void anotherExcludedOperation(
          @ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
    throw new RuntimeException("NotImplementedException");
  }
}
