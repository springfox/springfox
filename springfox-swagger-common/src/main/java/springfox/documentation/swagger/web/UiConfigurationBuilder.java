/*
 *
 *  Copyright 2018 the original author or authors.
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

import static springfox.documentation.builders.BuilderDefaults.defaultIfAbsent;

public class UiConfigurationBuilder {

  /*--------------------------------------------*\
   * Display
  \*--------------------------------------------*/
  private Boolean deepLinking;
  private Boolean displayOperationId;
  private Integer defaultModelsExpandDepth;
  private Integer defaultModelExpandDepth;
  private ModelRendering defaultModelRendering;
  private Boolean displayRequestDuration;
  private DocExpansion docExpansion;
  private Object filter; // Boolean=false OR String
  private Integer maxDisplayedTags;
  private OperationsSorter operationsSorter;
  private Boolean showExtensions;
  private Boolean showCommonExtensions;
  private TagsSorter tagsSorter;

  /*--------------------------------------------*\
   * Network
  \*--------------------------------------------*/
  private String[] supportedSubmitMethods;
  private String validatorUrl;

  private UiConfigurationBuilder() {
  }

  public static UiConfigurationBuilder builder() {
    return new UiConfigurationBuilder();
  }

  public UiConfiguration build() {
    return new UiConfiguration(
        defaultIfAbsent(deepLinking, true),
        defaultIfAbsent(displayOperationId, false),
        defaultIfAbsent(defaultModelsExpandDepth, 1),
        defaultIfAbsent(defaultModelExpandDepth, 1),
        defaultIfAbsent(defaultModelRendering, ModelRendering.EXAMPLE),
        defaultIfAbsent(displayRequestDuration, false),
        defaultIfAbsent(docExpansion, DocExpansion.NONE),
        defaultIfAbsent(filter, false),
        defaultIfAbsent(maxDisplayedTags, null),
        defaultIfAbsent(operationsSorter, OperationsSorter.ALPHA),
        defaultIfAbsent(showExtensions, false),
        defaultIfAbsent(showCommonExtensions, false),
        defaultIfAbsent(tagsSorter, TagsSorter.ALPHA),
        defaultIfAbsent(supportedSubmitMethods, UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS),
        defaultIfAbsent(validatorUrl, null)
    );
  }

  /**
   * @param deepLinking If set to true, enables deep linking for tags and operations. See the Deep Linking documentation
   *                    for more information.
   * @return this
   */
  public UiConfigurationBuilder deepLinking(Boolean deepLinking) {
    this.deepLinking = deepLinking;
    return this;
  }

  /**
   * @param displayOperationId Controls the display of operationId in operations list. The default is false.
   * @return this
   */
  public UiConfigurationBuilder displayOperationId(Boolean displayOperationId) {
    this.displayOperationId = displayOperationId;
    return this;
  }

  /**
   * @param defaultModelsExpandDepth The default expansion depth for models (set to -1 completely hide the models).
   * @return this
   */
  public UiConfigurationBuilder defaultModelsExpandDepth(Integer defaultModelsExpandDepth) {
    this.defaultModelsExpandDepth = defaultModelsExpandDepth;
    return this;
  }

  /**
   * @param defaultModelExpandDepth The default expansion depth for the model on the model-example section.
   * @return this
   */
  public UiConfigurationBuilder defaultModelExpandDepth(Integer defaultModelExpandDepth) {
    this.defaultModelExpandDepth = defaultModelExpandDepth;
    return this;
  }

  /**
   * @param defaultModelRendering Controls how the model is shown when the API is first rendered. (The user can always
   *                              switch the rendering for a given model by clicking the 'Model' and 'Example Value'
   *                              links.)
   * @return this
   */
  public UiConfigurationBuilder defaultModelRendering(ModelRendering defaultModelRendering) {
    this.defaultModelRendering = defaultModelRendering;
    return this;
  }

  /**
   * @param displayRequestDuration Controls the display of the request duration (in milliseconds) for Try-It-Out
   *                               requests.
   * @return this
   */
  public UiConfigurationBuilder displayRequestDuration(Boolean displayRequestDuration) {
    this.displayRequestDuration = displayRequestDuration;
    return this;
  }

  /**
   * @param docExpansion Controls the default expansion setting for the operations and tags. It can be 'list' (expands
   *                     only the tags), 'full' (expands the tags and operations) or 'none' (expands nothing).
   * @return this
   */
  public UiConfigurationBuilder docExpansion(DocExpansion docExpansion) {
    this.docExpansion = docExpansion;
    return this;
  }

  /**
   * @param filter If set, enables filtering. The top bar will show an edit box that you can use to filter the tagged
   *               operations that are shown. Can be Boolean to enable or disable, or a string, in which case filtering
   *               will be enabled using that string as the filter expression. Filtering is case sensitive matching the
   *               filter expression anywhere inside the tag.
   * @return this
   */
  public UiConfigurationBuilder filter(Object filter) {
    this.filter = filter;
    return this;
  }

  /**
   * @param maxDisplayedTags If set, limits the number of tagged operations displayed to at most this many. The default
   *                         is to show all operations.
   * @return this
   */
  public UiConfigurationBuilder maxDisplayedTags(Integer maxDisplayedTags) {
    this.maxDisplayedTags = maxDisplayedTags;
    return this;
  }

  /**
   * @param operationsSorter Apply a sort to the operation list of each API. It can be 'alpha' (sort by paths
   *                         alphanumerically), 'method' (sort by HTTP method) or a function (see Array.prototype.sort()
   *                         to know how sort function works). Default is the order returned by the server unchanged.
   * @return this
   */
  public UiConfigurationBuilder operationsSorter(OperationsSorter operationsSorter) {
    this.operationsSorter = operationsSorter;
    return this;
  }

  /**
   * @param showExtensions Controls the display of vendor extension (x-) fields and values for Operations, Parameters,
   *                       and Schema.
   * @return this
   */
  public UiConfigurationBuilder showExtensions(Boolean showExtensions) {
    this.showExtensions = showExtensions;
    return this;
  }
  
  /**
   * @param showCommonExtensions     Controls the display of extensions (pattern, maxLength, minLength, maximum, 
   *                                 minimum) fields and values for Parameters.
   * @return this
   */
  public UiConfigurationBuilder showCommonExtensions(Boolean showCommonExtensions) {
    this.showCommonExtensions = showCommonExtensions;
    return this;
  }

  /**
   * @param tagsSorter Apply a sort to the tag list of each API. It can be 'alpha' (sort by paths alphanumerically) or a
   *                   function (see Array.prototype.sort() to learn how to write a sort function). Two tag name strings
   *                   are passed to the sorter for each pass. Default is the order determined by Swagger-UI.
   * @return this
   */
  public UiConfigurationBuilder tagsSorter(TagsSorter tagsSorter) {
    this.tagsSorter = tagsSorter;
    return this;
  }

  /**
   * @param supportedSubmitMethods List of HTTP methods that have the Try it out feature enabled. An empty array
   *                               disables Try it out for all operations. This does not filter the operations from the
   *                               display.
   * @return this
   */
  public UiConfigurationBuilder supportedSubmitMethods(String[] supportedSubmitMethods) {
    this.supportedSubmitMethods = supportedSubmitMethods;
    return this;
  }

  /**
   * @param validatorUrl By default, Swagger-UI attempts to validate specs against swagger.io's online validator. You
   *                     can use this parameter to set a different validator URL, for example for locally deployed
   *                     validators (Validator Badge). Setting it to null will disable validation. This parameter is
   *                     relevant for Swagger 2.0 specs only.
   * @return this
   */
  public UiConfigurationBuilder validatorUrl(String validatorUrl) {
    this.validatorUrl = validatorUrl;
    return this;
  }
}
