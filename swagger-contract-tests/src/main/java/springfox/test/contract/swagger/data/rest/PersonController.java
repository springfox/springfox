/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.test.contract.swagger.data.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@BasePathAwareController
public class PersonController {
  @ApiOperation(value = "updateLastName", notes = "Updates the persons last name")
  @PatchMapping("/people/{id}")
  ResponseEntity<Void> updateLastName(
      @ApiParam(name = "lastName", value = "lastName parameter")
      @RequestParam("lastName") String lastName) {
    return ResponseEntity.ok(null);
  }

  @PatchMapping("/people/pageable")
  public ResponseEntity<Map<Long, Person>> pageableResults(
      final @PageableDefault(size = 20, sort = "id") Pageable pageable) {
    return ResponseEntity.ok(null);
  }

}
