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
  public static final UiConfiguration DEFAULT = new UiConfiguration(null);
  private final String validatorUrl;
  private final String docExpansion;
  private final String apiSorter;
  private final String defaultModelRendering;

  private final boolean enableJsonEditor;
  private final boolean showRequestHeaders;

  public UiConfiguration(String validatorUrl) {
    this(validatorUrl, "none", "alpha", "schema", false, true);
  }

  public UiConfiguration(
          String validatorUrl,
          String docExpansion,
          String apiSorter,
          String defaultModelRendering,
          boolean enableJsonEditor,
          boolean showRequestHeaders) {
    this.validatorUrl = validatorUrl;
    this.docExpansion = docExpansion;
    this.apiSorter = apiSorter;
    this.defaultModelRendering = defaultModelRendering;
    this.enableJsonEditor = enableJsonEditor;
    this.showRequestHeaders = showRequestHeaders;
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
  public String getApiSorter() {
    return apiSorter;
  }

  @JsonProperty("defaultModelRendering")
  public String getDefaultModelRendering() {
    return defaultModelRendering;
  }

  @JsonProperty("jsonEditor")
  public boolean isEnableJsonEditor() {
    return enableJsonEditor;
  }

  @JsonProperty("showRequestHeaders")
  public boolean isShowRequestHeaders() {
    return showRequestHeaders;
  }
}
