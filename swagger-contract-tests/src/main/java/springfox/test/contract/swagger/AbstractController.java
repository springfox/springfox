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

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class AbstractController<T extends RepresentationModel, ID> {

  @RequestMapping(value = "/create-t", method = RequestMethod.PUT)
  public void create(@RequestBody T toCreate) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "/get-t/{id}", method = RequestMethod.GET)
  @ApiResponses(value = {@ApiResponse(code = 405, message = "Invalid input")})
  public T get(@PathVariable("id")  ID id) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "/delete-t/{id}", method = RequestMethod.DELETE)
  @ApiResponses(value = {@ApiResponse(code = 405, message = "Invalid input")})
  public HttpEntity delete(@PathVariable("id")  ID id) {
    throw new UnsupportedOperationException();
  }
}
