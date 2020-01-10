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
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.test.contract.swagger.models.Pet;


@Controller
@RequestMapping("child")
@Api(value = "inheritedService", description = "Interface API")
public interface InheritedService {

    @RequestMapping(value = "child-method", method = RequestMethod.GET)
    String getSomething(String parameter);

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "kkj", response = Pet.class) })
    @RequestMapping(value = "/1575",
            produces = { "application/json" },
            method = RequestMethod.GET)
    Pet demonstrateInheritanceWithAnnotations(
        @ApiParam(value = "Parameter 1", required = true) @PathVariable("param1") String param1
                                             );

}