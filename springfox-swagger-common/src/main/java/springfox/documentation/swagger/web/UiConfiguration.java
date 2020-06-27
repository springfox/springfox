/*
 *
 *  Copyright 2017-2019 the original author or authors.
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

import static java.util.Optional.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UiConfiguration {
  /*--------------------------------------------*\
   * Display
  \*--------------------------------------------*/
  private final Boolean deepLinking;
  private final Boolean displayOperationId;
  private final Integer defaultModelsExpandDepth;
  private final Integer defaultModelExpandDepth;
  private final ModelRendering defaultModelRendering;
  private final Boolean displayRequestDuration;
  private final DocExpansion docExpansion;
  private final Object filter; // Boolean=false OR String
  private final Integer maxDisplayedTags;
  private final OperationsSorter operationsSorter;
  private final Boolean showExtensions;
  private final Boolean showCommonExtensions;
  private final TagsSorter tagsSorter;
  private final String validatorUrl;

  /*--------------------------------------------*\
   * Network
  \*--------------------------------------------*/
  private final String[] supportedSubmitMethods;
  private final String swaggerBaseUiUrl;

  /**
   * Default constructor
   *
   * @param deepLinking              If set to true, enables deep linking for tags and operations. See the Deep Linking
   *                                 documentation for more information.
   * @param displayOperationId       Controls the display of operationId in operations list. The default is false.
   * @param defaultModelsExpandDepth The default expansion depth for models (set to -1 completely hide the models).
   * @param defaultModelExpandDepth  The default expansion depth for the model on the model-example section.
   * @param defaultModelRendering    Controls how the model is shown when the API is first rendered. (The user can
   *                                 always switch the rendering for a given model by clicking the 'Model' and 'Example
   *                                 Value' links.)
   * @param displayRequestDuration   Controls the display of the request duration (in milliseconds) for Try-It-Out
   *                                 requests.
   * @param docExpansion             Controls the default expansion setting for the operations and tags. It can be
   *                                 'list' (expands only the tags), 'full' (expands the tags and operations) or 'none'
   *                                 (expands nothing).
   * @param filter                   If set, enables filtering. The top bar will show an edit box that you can use to
   *                                 filter the tagged operations that are shown. Can be Boolean to enable or disable,
   *                                 or a string, in which case filtering will be enabled using that string as the
   *                                 filter expression. Filtering is case sensitive matching the filter expression
   *                                 anywhere inside the tag.
   * @param maxDisplayedTags         If set, limits the number of tagged operations displayed to at most this many. The
   *                                 default is to show all operations.
   * @param operationsSorter         Apply a sort to the operation list of each API. It can be 'alpha' (sort by paths
   *                                 alphanumerically), 'method' (sort by HTTP method) or a function (see
   *                                 Array.prototype.sort() to know how sort function works). Default is the order
   *                                 returned by the server unchanged.
   * @param showExtensions           Controls the display of vendor extension (x-) fields and values for Operations,
   *                                 Parameters, and Schema.
   * @param showCommonExtensions     Controls the display of extensions (pattern, maxLength, minLength, maximum,
   *                                 minimum) fields and values for Parameters.
   * @param tagsSorter               Apply a sort to the tag list of each API. It can be 'alpha' (sort by paths
   *                                 alphanumerically) or a function (see Array.prototype.sort() to learn how to write a
   *                                 sort function). Two tag name strings are passed to the sorter for each pass.
   *                                 Default is the order determined by Swagger-UI.
   * @param supportedSubmitMethods   List of HTTP methods that have the Try it out feature enabled. An empty array
   *                                 disables Try it out for all operations. This does not filter the operations from
   *                                 the
   *                                 display.
   * @param validatorUrl             By default, Swagger-UI attempts to validate specs against swagger.io's online
   *                                 validator. You can use this parameter to set a different validator URL, for example
   *                                 for locally deployed validators (Validator Badge). Setting it to null will disable
   *                                 validation. This parameter is relevant for Swagger 2.0 specs only.
   */
  @SuppressWarnings("ParameterNumber")
  public UiConfiguration(
      Boolean deepLinking,
      Boolean displayOperationId,
      Integer defaultModelsExpandDepth,
      Integer defaultModelExpandDepth,
      ModelRendering defaultModelRendering,
      Boolean displayRequestDuration,
      DocExpansion docExpansion,
      Object filter,
      Integer maxDisplayedTags,
      OperationsSorter operationsSorter,
      Boolean showExtensions,
      Boolean showCommonExtensions,
      TagsSorter tagsSorter,
      String[] supportedSubmitMethods,
      String validatorUrl,
      String swaggerBaseUiUrl) {
    this.deepLinking = deepLinking;
    this.displayOperationId = displayOperationId;
    this.defaultModelsExpandDepth = defaultModelsExpandDepth;
    this.defaultModelExpandDepth = defaultModelExpandDepth;
    this.defaultModelRendering = defaultModelRendering;
    this.displayRequestDuration = displayRequestDuration;
    this.docExpansion = docExpansion;
    this.filter = filter;
    this.maxDisplayedTags = maxDisplayedTags;
    this.operationsSorter = operationsSorter;
    this.showExtensions = showExtensions;
    this.showCommonExtensions = showCommonExtensions;
    this.tagsSorter = tagsSorter;
    this.supportedSubmitMethods = supportedSubmitMethods;
    this.validatorUrl = validatorUrl;
    this.swaggerBaseUiUrl = swaggerBaseUiUrl;
  }

  @JsonProperty("deepLinking")
  public Boolean getDeepLinking() {
    return deepLinking;
  }

  @JsonProperty("displayOperationId")
  public Boolean getDisplayOperationId() {
    return displayOperationId;
  }

  @JsonProperty("defaultModelsExpandDepth")
  public Integer getDefaultModelsExpandDepth() {
    return defaultModelsExpandDepth;
  }

  @JsonProperty("defaultModelExpandDepth")
  public Integer getDefaultModelExpandDepth() {
    return defaultModelExpandDepth;
  }

  @JsonProperty("defaultModelRendering")
  public ModelRendering getDefaultModelRendering() {
    return defaultModelRendering;
  }

  @JsonProperty("displayRequestDuration")
  public Boolean getDisplayRequestDuration() {
    return displayRequestDuration;
  }

  @JsonProperty("docExpansion")
  public DocExpansion getDocExpansion() {
    return docExpansion;
  }

  @JsonProperty("filter")
  public Object getFilter() {
    return filter;
  }

  @JsonProperty("maxDisplayedTags")
  public Integer getMaxDisplayedTags() {
    return maxDisplayedTags;
  }

  @JsonProperty("operationsSorter")
  public OperationsSorter getOperationsSorter() {
    return operationsSorter;
  }

  @JsonProperty("showExtensions")
  public Boolean getShowExtensions() {
    return showExtensions;
  }

  @JsonProperty("showCommonExtensions")
  public Boolean getShowCommonExtensions() {
    return showCommonExtensions;
  }

  @JsonProperty("tagsSorter")
  public TagsSorter getTagsSorter() {
    return tagsSorter;
  }

  @JsonProperty("supportedSubmitMethods")
  public String[] getSupportedSubmitMethods() {
    return supportedSubmitMethods;
  }

  @JsonProperty("validatorUrl")
  public String getValidatorUrl() {
    return ofNullable(validatorUrl).orElse("");
  }

  @JsonProperty("swaggerBaseUiUrl")
  public String getSwaggerBaseUiUrl() {
    return swaggerBaseUiUrl;
  }

  @SuppressWarnings("java:S2386")
  public static class Constants {
    public static final String[] DEFAULT_SUBMIT_METHODS = new String[]{
        "get", "put", "post",
        "delete", "options", "head",
        "patch", "trace"};

    public static final String[] NO_SUBMIT_METHODS = new String[]{};
  }
}
