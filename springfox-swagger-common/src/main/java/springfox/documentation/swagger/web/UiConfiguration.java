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
package springfox.documentation.swagger.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UiConfiguration {
  static final UiConfiguration DEFAULT = new UiConfiguration(null);
  private final String validatorUrl;
  private final String docExpansion;
  private final String apisSorter;
  private final String defaultModelRendering;

  private final String[] supportedSubmitMethods;

  private final boolean jsonEditor;
  private final boolean showRequestHeaders;

  public UiConfiguration(String validatorUrl) {
    this(validatorUrl, "none", "alpha", "schema", Constants.DEFAULT_SUBMIT_METHODS, false, true);
  }

  public UiConfiguration(String validatorUrl, String[] supportedSubmitMethods) {
    this(validatorUrl, "none", "alpha", "schema", supportedSubmitMethods, false, true);
  }

  public UiConfiguration(
      String validatorUrl,
      String docExpansion,
      String apisSorter,
      String defaultModelRendering,
      String[] supportedSubmitMethods,
      boolean jsonEditor,
      boolean showRequestHeaders) {
    this.validatorUrl = validatorUrl;
    this.docExpansion = docExpansion;
    this.apisSorter = apisSorter;
    this.defaultModelRendering = defaultModelRendering;
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

  public static class Constants {
    public static final String[] DEFAULT_SUBMIT_METHODS = new String[] { "get", "post", "put", "delete", "patch" };
    public static final String[] NO_SUBMIT_METHODS = new String[] {};
  }
}
