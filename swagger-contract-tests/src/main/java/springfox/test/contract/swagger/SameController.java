/*
 *
 *  Copyright 2017 the original author or authors.
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

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.test.contract.swagger.models.ModelWithSameNameClasses;

@Controller
@RequestMapping("/same")
public class SameController {

  @RequestMapping(value = "/create-same", method = RequestMethod.PUT)
  public void create(@RequestBody ModelWithSameNameClasses toCreate) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "/get-same/{id}", method = RequestMethod.GET)
  @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") })
  public ModelWithSameNameClasses get(@PathVariable("id") String id) {
    throw new UnsupportedOperationException();
  }
}