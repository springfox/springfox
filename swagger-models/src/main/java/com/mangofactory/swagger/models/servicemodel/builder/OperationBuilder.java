package com.mangofactory.swagger.models.servicemodel.builder;

import com.mangofactory.swagger.models.servicemodel.Authorization;
import com.mangofactory.swagger.models.servicemodel.Operation;
import com.mangofactory.swagger.models.servicemodel.Parameter;
import com.mangofactory.swagger.models.servicemodel.ResponseMessage;

import java.util.List;
import java.util.Set;

public class OperationBuilder {
  private String method;
  private String summary;
  private String notes;
  private String responseClass;
  private String nickname;
  private int position;
  private List<String> produces;
  private List<String> consumes;
  private List<String> protocol;
  private List<Authorization> authorizations;
  private List<Parameter> parameters;
  private Set<ResponseMessage> responseMessages;
  private String deprecated;

  public OperationBuilder method(String method) {
    this.method = method;
    return this;
  }

  public OperationBuilder summary(String summary) {
    this.summary = summary;
    return this;
  }

  public OperationBuilder notes(String notes) {
    this.notes = notes;
    return this;
  }

  public OperationBuilder responseClass(String responseClass) {
    this.responseClass = responseClass;
    return this;
  }

  public OperationBuilder nickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

  public OperationBuilder position(int position) {
    this.position = position;
    return this;
  }

  public OperationBuilder produces(List<String> produces) {
    this.produces = produces;
    return this;
  }

  public OperationBuilder consumes(List<String> consumes) {
    this.consumes = consumes;
    return this;
  }

  public OperationBuilder protocol(List<String> protocol) {
    this.protocol = protocol;
    return this;
  }

  public OperationBuilder authorizations(List<Authorization> authorizations) {
    this.authorizations = authorizations;
    return this;
  }

  public OperationBuilder parameters(List<Parameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public OperationBuilder responseMessages(Set<ResponseMessage> responseMessages) {
    this.responseMessages = responseMessages;
    return this;
  }

  public OperationBuilder deprecated(String deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public Operation build() {
    return new Operation(method, summary, notes, responseClass, nickname, position, produces,
            consumes, protocol, authorizations, parameters, responseMessages, deprecated);
  }
}