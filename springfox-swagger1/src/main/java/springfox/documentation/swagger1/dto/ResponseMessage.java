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

package springfox.documentation.swagger1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResponseMessage {
  private int code;
  @JsonInclude(JsonInclude.Include.ALWAYS)
  private String message;
  private String responseModel;

  public ResponseMessage() {
  }

  public ResponseMessage(int code, String message, String responseModel) {
    this.code = code;
    this.message = message;
    this.responseModel = responseModel;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getResponseModel() {
    return responseModel;
  }

  public void setResponseModel(String responseModel) {
    this.responseModel = responseModel;
  }
}
