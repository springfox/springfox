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
import io.swagger.annotations.ResponseHeader;

public class ResponseHeaderTestController {

  public void noAnnotationHeaders() {
  }

  @ApiOperation(value = "operationHeadersOnly")
  @ApiResponses(value =
      {
          @ApiResponse(code = 200, message = "")
      }
  )
  public void defaultWithBoth() {
  }

  @ApiOperation(value = "operationHeadersOnly",
      responseHeaders =
      {
          @ResponseHeader(name = "header1", response = String.class)
      }
  )
  public void operationHeadersOnly() {
  }

  @ApiOperation(value = "operationHeadersOnly",
      responseHeaders =
          {
              @ResponseHeader(name = "header1", response = String.class, responseContainer = "List")
          }
  )
  public void operationHeadersOnlyCollection() {
  }

  @ApiResponses(value =
      {
          @ApiResponse(
              code = 200,
              message = "",
              responseHeaders = {
                  @ResponseHeader(name = "header1", response = String.class)
              })
      }
  )
  public void responseHeadersOnly() {
  }

  @ApiOperation(value = "operationHeadersOnly",
      responseHeaders =
          {
              @ResponseHeader(name = "header1", response = String.class)
          }
  )
  @ApiResponses(value =
      {
          @ApiResponse(
              code = 200,
              message = "",
              responseHeaders = {
                  @ResponseHeader(name = "header1", response = Integer.class)
              })
      }
  )
  public void bothWithOverride() {
  }

  @ApiOperation(value = "operationHeadersOnly",
      responseHeaders =
          {
              @ResponseHeader(name = "header1", response = String.class)
          }
  )
  @ApiResponses(value =
      {
          @ApiResponse(
              code = 200,
              message = "",
              responseHeaders = {
                  @ResponseHeader(name = "header2", response = Integer.class)
              })
      }
  )
  public void bothWithoutOverride() {
  }

}
