package com.mangofactory.service.model.builder;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mangofactory.service.model.Authorization;
import com.mangofactory.service.model.Operation;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.service.model.ResponseMessage;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

public class OperationBuilder {
  private String method;
  private String summary;
  private String notes;
  private String responseClass;
  private String nickname;
  private int position;
  private List<String> produces = newArrayList();
  private List<String> consumes = newArrayList();
  private List<String> protocol;
  private List<Authorization> authorizations = newArrayList();
  private List<Parameter> parameters = newArrayList();
  private Set<ResponseMessage> responseMessages = newHashSet();
  private String deprecated;
  private boolean isHidden;

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
    this.produces.addAll(produces);
    return this;
  }

  public OperationBuilder consumes(List<String> consumes) {
    this.consumes.addAll(consumes);
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
    this.parameters.addAll(parameters);
    return this;
  }

  public OperationBuilder responseMessages(Set<ResponseMessage> responseMessages) {
    //Add logic to consolidate the response messages
    ImmutableMap<Integer, ResponseMessage> responsesByCode = Maps.uniqueIndex(this.responseMessages,
            byStatusCode());
    Set<ResponseMessage> merged = newHashSet(this.responseMessages);
    for (ResponseMessage each : responseMessages) {
      if (responsesByCode.containsKey(each.getCode())) {
        ResponseMessage responseMessage = responsesByCode.get(each.getCode());
        String message = coalese(responseMessage.getMessage(), HttpStatus.OK.getReasonPhrase());
        String responseWithModel = coalese(responseMessage.getResponseModel(), each.getResponseModel());
        merged.remove(each);
        merged.add(new ResponseMessageBuilder()
                .code(each.getCode())
                .message(message)
                .responseModel(responseWithModel)
                .build());
      } else {
        merged.add(each);
      }
    }
    this.responseMessages = newHashSet(merged);
    return this;
  }

  private String coalese(String overrideMessage, String defaultMessage) {
    if (isNullOrEmpty(overrideMessage)) {
      return defaultMessage;
    }
    return overrideMessage;
  }
  private Function<? super ResponseMessage, Integer> byStatusCode() {
    return new Function<ResponseMessage, Integer>() {
      @Override
      public Integer apply(ResponseMessage input) {
        return input.getCode();
      }
    };
  }
  public OperationBuilder deprecated(String deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  public Operation build() {
    return new Operation(method, summary, notes, responseClass, nickname, position, produces,
            consumes, protocol, authorizations, parameters, responseMessages, deprecated, isHidden);
  }

  public OperationBuilder hidden(boolean isHidden) {
    this.isHidden = isHidden;
    return this;
  }
}