/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.swagger.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UiConfiguration {
  static final UiConfiguration DEFAULT = new UiConfiguration(null);
  private final String validatorUrl;
  private final String docExpansion;
  private final String apisSorter;
  private final String defaultModelRendering;
  private final Long requestTimeout;

  private final String[] supportedSubmitMethods;

  private final boolean jsonEditor;
  private final boolean showRequestHeaders;

  public UiConfiguration(String validatorUrl) {
    this(validatorUrl, "none", "alpha", "schema", Constants.DEFAULT_SUBMIT_METHODS, false, true, null);
  }

  public UiConfiguration(String validatorUrl, String[] supportedSubmitMethods) {
    this(validatorUrl, "none", "alpha", "schema", supportedSubmitMethods, false, true, null);
  }

  /** * Use the default constructor instead (with requestTimeout)
   * {@link UiConfiguration#UiConfiguration(String, String, String, String, String[], boolean, boolean, Long)} )}
   *
   * @param validatorUrl           By default, Swagger-UI attempts to validate specs against swagger.io's online
   *                               validator. You can use this parameter to set a different validator URL, for example
   *                               for locally deployed validators (Validator Badge). Setting it to null will disable
   *                               validation. This parameter is relevant for Swagger 2.0 specs only.
   * @param docExpansion           Controls how the API listing is displayed. It can be set to 'none' (default),
   *                               'list' (shows operations for each resource), or 'full' (fully expanded: shows
   *                               operations and their details).
   * @param apisSorter             Apply a sort to the API/tags list. It can be 'alpha' (sort by name) or a function
   *                               (see Array.prototype.sort() to know how sort function works). Default is the order
   *                               returned by the server unchanged.
   * @param defaultModelRendering  Controls how models are shown when the API is first rendered. (The user can
   *                               always switch the rendering for a given model by clicking the 'Model' and 'Model
   *                               Schema' links.) It can be set to 'model' or 'schema', and the default is 'schema'.
   * @param supportedSubmitMethods An array of of the HTTP operations that will have the 'Try it out!' option. An
   *                               empty array disables all operations. This does not filter the operations from the
   *                               display.
   * @param jsonEditor             Enables a graphical view for editing complex bodies. Defaults to false.
   * @param showRequestHeaders     Whether or not to show the headers that were sent when making a request via the
   *                               'Try it out!' option. Defaults to false.
   * @deprecated @since 2.6.1
   */
  @Deprecated
  public UiConfiguration(
      String validatorUrl,
      String docExpansion,
      String apisSorter,
      String defaultModelRendering,
      String[] supportedSubmitMethods,
      boolean jsonEditor,
      boolean showRequestHeaders) {
    this(
        validatorUrl,
        docExpansion,
        apisSorter,
        defaultModelRendering,
        supportedSubmitMethods,
        jsonEditor,
        showRequestHeaders,
        null);
  }

  /**
   * Default constructor
   *
   * @param validatorUrl           By default, Swagger-UI attempts to validate specs against swagger.io's online
   *                               validator. You can use this parameter to set a different validator URL, for example
   *                               for locally deployed validators (Validator Badge). Setting it to null will disable
   *                               validation. This parameter is relevant for Swagger 2.0 specs only.
   * @param docExpansion           Controls how the API listing is displayed. It can be set to 'none' (default),
   *                               'list' (shows operations for each resource), or 'full' (fully expanded: shows
   *                               operations and their details).
   * @param apisSorter             Apply a sort to the API/tags list. It can be 'alpha' (sort by name) or a function
   *                               (see Array.prototype.sort() to know how sort function works). Default is the order
   *                               returned by the server unchanged.
   * @param defaultModelRendering  Controls how models are shown when the API is first rendered. (The user can
   *                               always switch the rendering for a given model by clicking the 'Model' and 'Model
   *                               Schema' links.) It can be set to 'model' or 'schema', and the default is 'schema'.
   * @param supportedSubmitMethods An array of of the HTTP operations that will have the 'Try it out!' option. An
   *                               empty array disables all operations. This does not filter the operations from the
   *                               display.
   * @param jsonEditor             Enables a graphical view for editing complex bodies. Defaults to false.
   * @param showRequestHeaders     Whether or not to show the headers that were sent when making a request via the
   *                               'Try it out!' option. Defaults to false.
   * @param requestTimeout         - XHR timeout
   */
  public UiConfiguration(
      String validatorUrl,
      String docExpansion,
      String apisSorter,
      String defaultModelRendering,
      String[] supportedSubmitMethods,
      boolean jsonEditor,
      boolean showRequestHeaders,
      Long requestTimeout) {
    this.validatorUrl = validatorUrl;
    this.docExpansion = docExpansion;
    this.apisSorter = apisSorter;
    this.defaultModelRendering = defaultModelRendering;
    this.requestTimeout = requestTimeout;
    this.jsonEditor = jsonEditor;
    this.showRequestHeaders = showRequestHeaders;
    this.supportedSubmitMethods = supportedSubmitMethods;
  }

  @JsonProperty("validatorUrl")
  public String getValidatorUrl() {
    return validatorUrl;
  }

  @JsonProperty("docExpansion")
  public String getDocExpansion() {
    return docExpansion;
  }

  @JsonProperty("apisSorter")
  public String getApisSorter() {
    return apisSorter;
  }

  @JsonProperty("defaultModelRendering")
  public String getDefaultModelRendering() {
    return defaultModelRendering;
  }

  @JsonProperty("supportedSubmitMethods")
  public String[] getSupportedSubmitMethods() {
    return supportedSubmitMethods;
  }

  @JsonProperty("jsonEditor")
  public boolean isJsonEditor() {
    return jsonEditor;
  }

  @JsonProperty("showRequestHeaders")
  public boolean isShowRequestHeaders() {
    return showRequestHeaders;
  }

  @JsonProperty("requestTimeout")
  public Long getRequestTimeout() {
    return requestTimeout;
  }

  public static class Constants {
    public static final String[] DEFAULT_SUBMIT_METHODS = new String[] { "get", "post", "put", "delete", "patch" };
    public static final String[] NO_SUBMIT_METHODS = new String[] {};
  }
}
