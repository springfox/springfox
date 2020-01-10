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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Api(description = "Services to demonstrate produces/consumes override behaviour on document and operation level")
@RequestMapping(path = "/consumes-produces")
public class ConsumesProducesService {

  @GetMapping("/without-operation-produces")
  @ApiOperation("Does not have operation produces defined")
  public String withoutOperationProduces() {
    throw new UnsupportedOperationException();
  }

  @GetMapping(value = "/with-operation-produces", produces = MediaType.APPLICATION_XML_VALUE)
  @ApiOperation("Does have operation produces defined")
  public String withOperationProduces() {
    throw new UnsupportedOperationException();
  }

  @PostMapping("/without-operation-consumes")
  @ApiOperation("Does not have operation consumes defined")
  public void withoutOperationConsumes(@RequestBody String test) {
    throw new UnsupportedOperationException();
  }

  @PostMapping(value = "/with-operation-consumes", consumes = MediaType.APPLICATION_XML_VALUE)
  @ApiOperation("Does have operation consumes defined")
  public void withOperationConsumes(@RequestBody String test) {
    throw new UnsupportedOperationException();
  }

  @PostMapping(value = "/with-operation-consumes-produces", consumes = MediaType.APPLICATION_XML_VALUE, produces =
      MediaType.APPLICATION_XML_VALUE)
  @ApiOperation("Does have operation consumes and produces defined")
  public void withOperationConsumesAndProduces(@RequestBody String test) {
    throw new UnsupportedOperationException();
  }
}
