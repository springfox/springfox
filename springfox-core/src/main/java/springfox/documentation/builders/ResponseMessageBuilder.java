/*
 *
 *  Copyright 2017 the original author or authors.
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

import springfox.documentation.schema.Example;
import springfox.documentation.service.Header;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static springfox.documentation.builders.BuilderDefaults.*;

/**
 * Use {@link ResponseBuilder} instead
 * @deprecated @since 3.0.0
 */
@Deprecated
public class ResponseMessageBuilder {
  private int code;
  private String message;
  private springfox.documentation.schema.ModelReference responseModel;
  private final List<Example> examples = new ArrayList<>();
  private final Map<String, Header> headers = new TreeMap<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

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
   * @return this
   */
  public ResponseMessageBuilder message(String message) {
    this.message = defaultIfAbsent(message, this.message);
    return this;
  }

  /**
   * Updates the model the response represents
   *
   * @param responseModel - model reference
   * @return this
   */
  public ResponseMessageBuilder responseModel(springfox.documentation.schema.ModelReference responseModel) {
    this.responseModel = defaultIfAbsent(responseModel, this.responseModel);
    return this;
  }

  /**
   * Updates the response examples
   *
   * @param examples response examples
   * @return this
   * @since 3.0.0
   */
  public ResponseMessageBuilder examples(List<Example> examples) {
    this.examples.addAll(nullToEmptyList(examples));
    return this;
  }

  /**
   * Updates the response headers
   *
   * @param headers headers with description
   * @return this
   * @since 2.5.0
   */
  public ResponseMessageBuilder headersWithDescription(Map<String, Header> headers) {
    this.headers.putAll(nullToEmptyMap(headers));
    return this;
  }

  /**
   * Updates the response message extensions
   *
   * @param extensions - response message extensions
   * @return this
   * @since 2.5.0
   */
  public ResponseMessageBuilder vendorExtensions(List<VendorExtension> extensions) {
    this.vendorExtensions.addAll(nullToEmptyList(extensions));
    return this;
  }

  public springfox.documentation.service.ResponseMessage build() {
    return new springfox.documentation.service.ResponseMessage(
        code,
        message,
        responseModel,
        examples,
        headers,
        vendorExtensions);
  }
}