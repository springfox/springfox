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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.spring.web.dummy.models.EnumCollection;
import springfox.documentation.spring.web.dummy.models.EnumType;

@Controller
@RequestMapping("/enums")
@Api(value = "", description = "Service that return enums")
public class EnumService {

    @RequestMapping(value = "/wrapped", method = RequestMethod.GET)
    @ApiOperation(value = "Example with wrapped enum collection")
    public EnumCollection getCollectionValue() {
        EnumCollection result = new EnumCollection();
        result.setTypes(new HashSet<>(Arrays.asList(EnumType.ONE, EnumType.TWO)));
        return result;
    }

    @RequestMapping(value = "/entity", method = RequestMethod.GET)
    @ApiOperation(value = "Example with response entity single value")
    public ResponseEntity<EnumType> getResponseEntityValue() {
        return new ResponseEntity<EnumType>(EnumType.ONE, HttpStatus.OK);
    }

    @RequestMapping(value = "/collection", method = RequestMethod.GET)
    @ApiOperation(value = "Example with response entity collection")
    public ResponseEntity<Set<EnumType>> getResponseEntityCollection() {
        return new ResponseEntity<Set<EnumType>>(Collections.singleton(EnumType.ONE), HttpStatus.OK);
    }

}
