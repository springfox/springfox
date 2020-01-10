/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.test.contract.swagger.models.EnumCollection;
import springfox.test.contract.swagger.models.EnumType;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

@Controller
@RequestMapping("/enums")
@Api(value = "", description = "Service that return enums")
public class EnumService {

  @RequestMapping(value = "/wrapped", method = RequestMethod.GET)
  @ApiOperation(value = "Example with wrapped enum collection")
  public EnumCollection getCollectionValue() {
    EnumCollection result = new EnumCollection();
    result.setTypes(Stream.of(
        EnumType.ONE,
        EnumType.TWO).collect(toSet()));
    return result;
  }

  @RequestMapping(value = "/entity", method = RequestMethod.GET)
  @ApiOperation(value = "Example with response entity single value")
  public ResponseEntity<EnumType> getResponseEntityValue() {
    return new ResponseEntity<>(
        EnumType.ONE,
        HttpStatus.OK);
  }

  @RequestMapping(value = "/collection", method = RequestMethod.GET)
  @ApiOperation(value = "Example with response entity collection")
  public ResponseEntity<Set<EnumType>> getResponseEntityCollection() {
    return new ResponseEntity<>(
        singleton(EnumType.ONE),
        HttpStatus.OK);
  }

}
