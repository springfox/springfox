/*
 *
 *  Copyright 2020 the original author or authors.
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.test.contract.swagger.models.City;
import springfox.test.contract.swagger.models.Country;
import springfox.test.contract.swagger.models.District;
import springfox.test.contract.swagger.models.House;
import springfox.test.contract.swagger.models.Region;
import springfox.test.contract.swagger.models.Street;

import java.util.List;

@RestController
@RequestMapping("/cyclic-structures")
public class CyclicStructuresController {

  @RequestMapping(value = "/create-street", method = RequestMethod.PUT)
  public List<House> createCyclicStreet(@RequestBody Street street) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "/create-city", method = RequestMethod.PUT)
  public List<District> createCyclicCity(@RequestBody City city) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "/create-country", method = RequestMethod.PUT)
  @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") })
  public List<Region> createCyclicCountry(@RequestBody Country country) {
    throw new UnsupportedOperationException();
  }

}
