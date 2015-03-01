package com.mangofactory.documentation.builders;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mangofactory.documentation.schema.ModelRef;
import com.mangofactory.documentation.service.Authorization;
import com.mangofactory.documentation.service.Operation;
import com.mangofactory.documentation.service.Parameter;
import com.mangofactory.documentation.service.ResponseMessage;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class OperationBuilder {
  private String method;
  private String summary;
  private String notes;
  private String responseClass;
  private String nickname;
  private int position;
  private Set<String> produces = newHashSet();
  private Set<String> consumes = newHashSet();
  private Set<String> protocol = newHashSet();
  private List<Authorization> authorizations = newArrayList();
  private List<Parameter> parameters = newArrayList();
  private Set<ResponseMessage> responseMessages = newHashSet();
  private Set<String> tags = newHashSet();
  private String deprecated;
  private boolean isHidden;
  private ModelRef responseModel;

  public OperationBuilder method(String method) {
    this.method = defaultIfAbsent(method, this.method);
    return this;
  }

  public OperationBuilder summary(String summary) {
    this.summary = defaultIfAbsent(summary, this.summary);
    return this;
  }

  public OperationBuilder notes(String notes) {
    this.notes = defaultIfAbsent(notes, this.notes);
    return this;
  }

  public OperationBuilder responseClass(String responseClass) {
    this.responseClass = defaultIfAbsent(responseClass, this.responseClass);
    return this;
  }

  public OperationBuilder nickname(String nickname) {
    this.nickname = defaultIfAbsent(nickname, this.nickname);
    return this;
  }

  public OperationBuilder position(int position) {
    this.position = position;
    return this;
  }

  public OperationBuilder produces(Set<String> produces) {
    this.produces.addAll(nullToEmptySet(produces));
    return this;
  }

  public OperationBuilder consumes(Set<String> consumes) {
    this.consumes.addAll(nullToEmptySet(consumes));
    return this;
  }

  public OperationBuilder protocols(Set<String> protocols) {
    this.protocol.addAll(nullToEmptySet(protocols));
    return this;
  }

  public OperationBuilder authorizations(List<Authorization> authorizations) {
    this.authorizations.addAll(nullToEmptyList(authorizations));
    return this;
  }

  public OperationBuilder parameters(List<Parameter> parameters) {
    this.parameters.addAll(nullToEmptyList(parameters));
    return this;
  }

  public OperationBuilder responseMessages(Set<ResponseMessage> responseMessages) {
    this.responseMessages = newHashSet(mergeResponseMessages(responseMessages));
    return this;
  }

  public OperationBuilder deprecated(String deprecated) {
    this.deprecated = defaultIfAbsent(deprecated, this.deprecated);
    return this;
  }

  public OperationBuilder hidden(boolean isHidden) {
    this.isHidden = isHidden;
    return this;
  }

  public OperationBuilder responseModel(ModelRef responseType) {
    this.responseModel = defaultIfAbsent(responseType, this.responseModel);
    return this;
  }
  
  public OperationBuilder tags(Set<String> tags) {
    this.tags.addAll(nullToEmptySet(tags));
    return this;
  }

  public Operation build() {
    return new Operation(method, summary, notes, responseModel, nickname, position, tags, produces,
            consumes, protocol, authorizations, parameters, responseMessages, deprecated, isHidden);
  }

  private Set<ResponseMessage> mergeResponseMessages(Set<ResponseMessage> responseMessages) {
    //Add logic to consolidate the response messages
    ImmutableMap<Integer, ResponseMessage> responsesByCode = Maps.uniqueIndex(this.responseMessages, byStatusCode());
    Set<ResponseMessage> merged = newHashSet(this.responseMessages);
    for (ResponseMessage each : responseMessages) {
      if (responsesByCode.containsKey(each.getCode())) {
        ResponseMessage responseMessage = responsesByCode.get(each.getCode());
        String message = defaultIfAbsent(Strings.emptyToNull(responseMessage.getMessage()), HttpStatus.OK.getReasonPhrase());
        ModelRef responseWithModel = defaultIfAbsent(responseMessage.getResponseModel(),
                each.getResponseModel());
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
    return merged;
  }

  private Function<? super ResponseMessage, Integer> byStatusCode() {
    return new Function<ResponseMessage, Integer>() {
      @Override
      public Integer apply(ResponseMessage input) {
        return input.getCode();
      }
    };
  }
}