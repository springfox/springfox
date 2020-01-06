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

package springfox.documentation.swagger1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@JsonPropertyOrder({
    "method", "summary", "notes", "type", "nickname", "produces",
    "consumes", "parameters", "responseMessages", "deprecated"
})
public class Operation {
  private String method;
  private String summary;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String notes;
  @JsonProperty
  @JsonUnwrapped
  private SwaggerDataType dataType;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String nickname;
  @JsonIgnore
  private int position;
  private List<String> produces;
  private List<String> consumes;
  @JsonIgnore
  private List<String> protocol;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, List<AuthorizationScope>> authorizations;
  private List<Parameter> parameters;
  private SortedSet<ResponseMessage> responseMessages;
  private String deprecated;

  public Operation() {
  }

  @SuppressWarnings("ParameterNumber")
  public Operation(
      String method,
      String summary,
      String notes,
      String responseClass,
      String nickname,
      int position,
      List<String> produces,
      List<String> consumes,
      List<String> protocol,
      List<Authorization> authorizations,
      List<Parameter> parameters,
      Set<ResponseMessage> responseMessages,
      String deprecated) {
    this.method = method;
    this.summary = summary;
    this.notes = notes;
    this.dataType = new DataType(responseClass);
    this.nickname = nickname;
    this.position = position;
    this.produces = produces;
    this.consumes = consumes;
    this.protocol = protocol;
    this.authorizations = toAuthorizationsMap(authorizations);
    this.parameters = parameters.stream()
        .sorted(byName()).collect(toList());
    this.responseMessages =
        responseMessages.stream()
            .collect(Collectors.toCollection(() -> new TreeSet<>(responseMessageOrdering())));
    this.deprecated = deprecated;
  }

  private Comparator<ResponseMessage> responseMessageOrdering() {
    return Comparator.comparingInt(ResponseMessage::getCode);
  }

  private Map<String, List<AuthorizationScope>> toAuthorizationsMap(List<Authorization> authorizations) {
    return authorizations.stream()
        .collect(toMap(Authorization::getType, value -> new ArrayList<>(value.getScopes())));
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }


  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public List<String> getProduces() {
    return produces;
  }

  public void setProduces(List<String> produces) {
    this.produces = produces;
  }

  public List<String> getConsumes() {
    return consumes;
  }

  public void setConsumes(List<String> consumes) {
    this.consumes = consumes;
  }

  public List<String> getProtocol() {
    return protocol;
  }

  public void setProtocol(List<String> protocol) {
    this.protocol = protocol;
  }

  public Map<String, List<AuthorizationScope>> getAuthorizations() {
    return authorizations;
  }

  public void setAuthorizations(Map<String, List<AuthorizationScope>> authorizations) {
    this.authorizations = authorizations;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<Parameter> parameters) {
    this.parameters = parameters;
  }

  public Set<ResponseMessage> getResponseMessages() {
    return responseMessages;
  }

  public void setResponseMessages(Set<ResponseMessage> responseMessages) {
    this.responseMessages =
        responseMessages.stream()
            .collect(Collectors.toCollection(() -> new TreeSet<>(responseMessageOrdering())));
  }

  public String getDeprecated() {
    return deprecated;
  }

  public void setDeprecated(String deprecated) {
    this.deprecated = deprecated;
  }

  public void setDataType(SwaggerDataType dataType) {
    this.dataType = dataType;
  }

  private Comparator<Parameter> byName() {
    return Comparator.comparing(Parameter::getName);
  }
}
