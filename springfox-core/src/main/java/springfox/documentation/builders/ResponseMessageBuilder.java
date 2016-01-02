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

package springfox.documentation.builders;

import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.ResponseMessage;

public class ResponseMessageBuilder {
  private int code;
  private String message;
  private ModelReference responseModel;

  /**
   * Updates the http response code
   *
   * @param code - response code
   * @return this
   */
  public ResponseMessageBuilder code(int code) {
    this.code = code;
    return this;
  }

  /**
   * Updates the response message
   *
   * @param message - message
   * @return
   */
  public ResponseMessageBuilder message(String message) {
    this.message = BuilderDefaults.defaultIfAbsent(message, this.message);
    return this;
  }

  /**
   * Updates the model the response represents
   *
   * @param responseModel - model reference
   * @return this
   */
  public ResponseMessageBuilder responseModel(ModelReference responseModel) {
    this.responseModel = BuilderDefaults.defaultIfAbsent(responseModel, this.responseModel);
    return this;
  }

  public ResponseMessage build() {
    return new ResponseMessage(code, message, responseModel);
  }
}