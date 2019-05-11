/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.service;


import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import springfox.documentation.schema.ModelReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

public class Operation implements Ordered {
  private final HttpMethod method;
  private final String summary;
  private final String notes;
  private final ModelReference responseModel;
  private final String uniqueId;
  private final int position;
  private final Set<String> tags;
  private final Set<String> produces;
  private final Set<String> consumes;
  private final Set<String> protocol;
  private final boolean isHidden;
  private final Map<String, List<AuthorizationScope>> securityReferences;
  private final List<Parameter> parameters;
  private final Set<ResponseMessage> responseMessages;
  private final Set<RequestParameter> requestParameters;
  private final RequestBody body;
  private final Set<Response> responses;
  private final String deprecated;
  private final List<VendorExtension> vendorExtensions;

  @SuppressWarnings("ParameterNumber")
  public Operation(
      HttpMethod method,
      String summary,
      String notes,
      ModelReference responseModel,
      String uniqueId,
      int position,
      Set<String> tags,
      Set<String> produces,
      Set<String> consumes,
      Set<String> protocol,
      List<SecurityReference> securityReferences,
      List<Parameter> parameters,
      Set<ResponseMessage> responseMessages,
      String deprecated,
      boolean isHidden,
      Collection<VendorExtension> vendorExtensions,
      Set<RequestParameter> requestParameters,
      RequestBody body,
      Set<Response> responses) {

    this.method = method;
    this.summary = summary;
    this.notes = notes;
    this.responseModel = responseModel;
    this.uniqueId = uniqueId;
    this.position = position;
    this.tags = tags;
    this.produces = produces;
    this.consumes = consumes;
    this.protocol = protocol;
    this.requestParameters = requestParameters;
    this.responses = responses;
    this.isHidden = isHidden;
    this.securityReferences = toAuthorizationsMap(securityReferences);
    this.parameters = parameters.stream()
        .sorted(byOrder().thenComparing(byParameterName())).collect(toList());
    this.responseMessages = responseMessages;
    this.deprecated = deprecated;
    this.body = body;
    this.vendorExtensions = new ArrayList<>(vendorExtensions);
  }

  public boolean isHidden() {
    return isHidden;
  }

  /**
   * @deprecated @since 3.0.0
   * Use @see {@link Operation#getResponses()}
   * @return model reference
   */
  @Deprecated
  public ModelReference getResponseModel() {
    return responseModel;
  }

  public Set<String> getTags() {
    return tags;
  }

  private Map<String, List<AuthorizationScope>> toAuthorizationsMap(List<SecurityReference> securityReferences) {
    return securityReferences.stream()
        .collect(toMap(SecurityReference::getReference, value -> new ArrayList<>(value.getScopes())));
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getSummary() {
    return summary;
  }

  public String getNotes() {
    return notes;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public int getPosition() {
    return position;
  }

  public Set<String> getProduces() {
    return produces;
  }

  public Set<String> getConsumes() {
    return consumes;
  }

  public Set<String> getProtocol() {
    return protocol;
  }

  public Map<String, List<AuthorizationScope>> getSecurityReferences() {
    return securityReferences;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  /**
   * @deprecated @since 3.0.0
   * Use @see {@link Operation#getResponses()}
   * @return model reference
   */
  @Deprecated
  public Set<ResponseMessage> getResponseMessages() {
    return responseMessages;
  }

  public Set<Response> getResponses() {
    return responses;
  }

  public String getDeprecated() {
    return deprecated;
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }

  private Comparator<Parameter> byOrder() {
    return Comparator.comparingInt(Parameter::getOrder);
  }

  private Comparator<Parameter> byParameterName() {
    return Comparator.comparing(Parameter::getName);
  }

  @Override
  public int getOrder() {
    return position;
  }

  public Set<RequestParameter> getRequestParameters() {
    return requestParameters;
  }

  public RequestBody getBody() {
    return body;
  }
}
