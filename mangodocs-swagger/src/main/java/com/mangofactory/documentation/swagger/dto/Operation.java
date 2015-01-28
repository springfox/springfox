package com.mangofactory.documentation.swagger.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Function;
import com.google.common.primitives.Ints;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import static com.google.common.collect.ImmutableSortedSet.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

@JsonPropertyOrder({
        "method", "summary", "notes", "type", "nickname", "produces",
        "consumes", "parameters", "responseMessages", "deprecated"
})
public class Operation {
  private String method;
  private String summary;
  private String notes;
  @JsonIgnore
  private String responseClass;
  @JsonProperty
  @JsonUnwrapped
  private SwaggerDataType dataType;
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

  public Operation(String method, String summary, String notes, String responseClass, String nickname, int position,
                   List<String> produces, List<String> consumes, List<String> protocol,
                   List<Authorization> authorizations,
                   List<Parameter> parameters, Set<ResponseMessage> responseMessages,
                   String deprecated) {
    this.method = method;
    this.summary = summary;
    this.notes = notes;
    this.responseClass = responseClass;
    this.dataType = new DataType(responseClass);
    this.nickname = nickname;
    this.position = position;
    this.produces = produces;
    this.consumes = consumes;
    this.protocol = protocol;
    this.authorizations = toAuthorizationsMap(authorizations);
    this.parameters = parameters;
    this.responseMessages = copyOf(responseMessageOrdering(), responseMessages);
    this.deprecated = deprecated;
  }

  private Comparator<ResponseMessage> responseMessageOrdering() {
    return new Comparator<ResponseMessage>() {
      @Override
      public int compare(ResponseMessage first, ResponseMessage second) {
        return Ints.compare(first.getCode(), second.getCode());
      }
    };
  }

  private Map<String, List<AuthorizationScope>> toAuthorizationsMap(List<Authorization> authorizations) {
    return transformEntries(uniqueIndex(authorizations, byType()), toScopes());
  }

  private EntryTransformer<? super String, ? super Authorization, List<AuthorizationScope>> toScopes() {
    return new EntryTransformer<String, Authorization, List<AuthorizationScope>>() {
      @Override
      public List<AuthorizationScope> transformEntry(String key, Authorization value) {
        return newArrayList(value.getScopes());
      }
    };
  }

  private Function<? super Authorization, String> byType() {
    return new Function<Authorization, String>() {
      @Override
      public String apply(Authorization input) {
        return input.getType();
      }
    };
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

  public String getResponseClass() {
    return responseClass;
  }

  public void setResponseClass(String responseClass) {
    this.responseClass = responseClass;
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
    this.responseMessages = copyOf(responseMessageOrdering(), responseMessages);
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
}
