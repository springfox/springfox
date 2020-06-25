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
import springfox.documentation.common.ExternalDocumentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@SuppressWarnings("deprecation")
public class Operation implements Ordered {
  private final HttpMethod method;
  private final String summary;
  private final String notes;
  private final ExternalDocumentation externalDocumentation;
  private final String uniqueId;
  private final Set<String> tags;
  private final Map<String, List<AuthorizationScope>> securityReferences;
  private final Set<RequestParameter> requestParameters;
  private final RequestBody body;
  private final Set<Response> responses;
  private final String deprecated;
  private final List<VendorExtension> vendorExtensions;

  //TODO: to be deprecated
  private final int position;
  private final List<Parameter> parameters;
  private final Set<ResponseMessage> responseMessages;
  private final springfox.documentation.schema.ModelReference responseModel;
  private final Set<String> produces;
  private final Set<String> consumes;
  private final Set<String> protocol;
  private final boolean isHidden;

  @SuppressWarnings("ParameterNumber")
  public Operation(
      HttpMethod method,
      String summary,
      String notes,
      ExternalDocumentation externalDocumentation,
      springfox.documentation.schema.ModelReference responseModel,
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
    this.externalDocumentation = externalDocumentation;
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
        .sorted(byOrder().thenComparing(byParameterName()))
        .collect(toList());
    this.responseMessages = responseMessages;
    this.deprecated = deprecated;
    this.body = body;
    this.vendorExtensions = new ArrayList<>(vendorExtensions);
  }

  public boolean isHidden() {
    return isHidden;
  }

  /**
   * @return model reference
   * @deprecated @since 3.0.0
   * Use @see {@link Operation#getResponses()}
   */
  @Deprecated
  public springfox.documentation.schema.ModelReference getResponseModel() {
    return responseModel;
  }

  public Set<String> getTags() {
    return tags;
  }

  private Map<String, List<AuthorizationScope>> toAuthorizationsMap(List<SecurityReference> securityReferences) {
    return securityReferences.stream()
        .collect(toMap(
            SecurityReference::getReference,
            value -> new ArrayList<>(value.getScopes())));
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
   * @return model reference
   * @deprecated @since 3.0.0
   * Use @see {@link Operation#getResponses()}
   */
  @Deprecated
  public Set<ResponseMessage> getResponseMessages() {
    return responseMessages;
  }

  public SortedSet<Response> getResponses() {
    return responses.stream()
        .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Response::getCode))));
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

  public SortedSet<RequestParameter> getQueryParameters() {
    return requestParameters.stream()
        .filter(r -> !r.getParameterSpecification().getContent().isPresent())
        .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RequestParameter::getParameterIndex)
            .thenComparing(RequestParameter::getName))));
  }

  public RequestBody getBody() {
    return requestParameters.stream()
        .filter(r -> r.getParameterSpecification().getContent().isPresent())
        .findFirst()
        .map(this::toBody)
        .orElse(body);
  }

  private RequestBody toBody(RequestParameter parameter) {
    return new RequestBody(
        parameter.getDescription(),
        parameter.getParameterSpecification().getContent()
            .map(ContentSpecification::getRepresentations)
            .orElse(new HashSet<>()), //TODO: Log this?
        parameter.getRequired(),
        parameter.getExtensions());
  }

  public ExternalDocumentation getExternalDocumentation() {
    return externalDocumentation;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Operation.class.getSimpleName() + "[", "]")
        .add("method=" + method)
        .add("summary='" + summary + "'")
        .add("notes='" + notes + "'")
        .add("externalDocumentation=" + externalDocumentation)
        .add("uniqueId='" + uniqueId + "'")
        .add("position=" + position)
        .add("tags=" + tags)
        .add("responseModel=" + responseModel)
        .add("produces=" + produces)
        .add("consumes=" + consumes)
        .add("protocol=" + protocol)
        .add("isHidden=" + isHidden)
        .add("securityReferences=" + securityReferences)
        .add("parameters=" + parameters)
        .add("responseMessages=" + responseMessages)
        .add("requestParameters=" + requestParameters)
        .add("body=" + body)
        .add("responses=" + responses)
        .add("deprecated='" + deprecated + "'")
        .add("vendorExtensions=" + vendorExtensions)
        .toString();
  }
}
