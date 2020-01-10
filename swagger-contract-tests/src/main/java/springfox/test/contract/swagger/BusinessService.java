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
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.test.contract.swagger.models.Business;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@Controller
@Api(value = "/", description = "Services to demonstrate path variable resolution")
@RequestMapping(produces = {MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE},
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class BusinessService {

  @RequestMapping(value = "/businesses/aliased/{otherId}", method = RequestMethod.GET)
  @ApiOperation(value = "Find a business by its id", nickname = "findBusinessById")
  public String getAliasedPathVariable(
          @ApiParam(value = "ID of business", required = true) @PathVariable("otherId") String businessId) {
    return "This is only a test";
  }

  @RequestMapping(value = "/businesses/non-aliased/{businessId}", method = RequestMethod.GET)
  @ApiOperation(value = "Find a business by its id", nickname = "findBusinessById",
          authorizations = @Authorization(value = "oauth2",
                  scopes = {@AuthorizationScope(scope = "scope", description = "scope description")
                  }))
  public String getNonAliasedPathVariable(
          @ApiParam(value = "ID of business", required = true) @PathVariable("businessId") String businessId) {
    return "This is only a test";
  }

  @RequestMapping(value = "/businesses/vanilla/{businessId}", method = RequestMethod.GET)
  public String getVanillaPathVariable(@PathVariable String businessId) {
    return "This is only a test";
  }

  @RequestMapping(value = "/businesses/responseEntity/{businessId}", method = RequestMethod.GET)
  public ResponseEntity<String> getResponseEntity(@PathVariable String businessId) {
    return new ResponseEntity<String>("This is only a test", HttpStatus.OK);
  }

  @RequestMapping(value = { "/businesses/typeEcho" }, method = POST, consumes = APPLICATION_JSON_VALUE,
          produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Business.BusinessType> businessTypeEcho(@RequestBody Business.BusinessType business) {
    return new ResponseEntity<>(
        Business.BusinessType.PRODUCT,
        OK);
  }

  @RequestMapping(value = { "/businesses/demonstratesApiModelName" }, method = POST, consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Business", response = Business.class)})
  public String businessAsString() {
    return "";
  }

  @RequestMapping(value = {"/businesses/byTypes"}, method = GET, produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public List<Business> businessesByCategories(@RequestParam Business.BusinessType[] types) {
    return new ArrayList<>();
  }
}