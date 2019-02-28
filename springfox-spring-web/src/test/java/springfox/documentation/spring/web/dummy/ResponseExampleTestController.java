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
package springfox.documentation.spring.web.dummy;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;

public class ResponseExampleTestController {

    @ApiOperation(value = "operationWithOneExample")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "")})
    public void operationWithNoExamples() {
        // An endpoint with no Examples
    }

    @ApiOperation(value = "operationWithOneExample")
    @ApiResponses(value =
            {
                    @ApiResponse(code = 200, message = "", examples = @Example(
                            value = @ExampleProperty(
                                    value = "value", mediaType = "mediaType")))})
    public void operationWithOneExample() {
        // An endpoint with one Example
    }

    @ApiOperation(value = "operationWithTwoExamples")
    @ApiResponses(value =
            {
                    @ApiResponse(code = 200, message = "", examples = @Example(
                            value = {
                                    @ExampleProperty(
                                            value = "value1", mediaType = "mediaType1"),
                                    @ExampleProperty(
                                            value = "value2", mediaType = "mediaType2")}))})
    public void operationWithTwoExamples() {
        // An endpoint with two Examples
    }

    @ApiOperation(value = "operationWithEmptyExample")
    @ApiResponses(value =
            {
                    @ApiResponse(code = 200, message = "", examples = @Example(
                            value = {
                                    @ExampleProperty(
                                            value = "value1", mediaType = "mediaType1"),
                                    @ExampleProperty(
                                            value = "", mediaType = "mediaType2")}))})
    public void operationWithEmptyExample() {
        // An endpoint with an Example that has an empty value
    }

}
