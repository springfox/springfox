package com.mangofactory.documentation.service.model.builder;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mangofactory.documentation.service.model.Authorization;
import com.mangofactory.documentation.service.model.Operation;
import com.mangofactory.documentation.service.model.Parameter;
import com.mangofactory.documentation.service.model.ResponseMessage;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;

import static com.mangofactory.documentation.service.model.builder.BuilderDefaults.*;

public class OperationBuilder {
  private String method;
  private String summary;
  private String notes;
  private String responseClass;
  private String nickname;
  private int position;
  private Set<String> produces = Sets.newHashSet();
  private Set<String> consumes = Sets.newHashSet();
  private Set<String> protocol = Sets.newHashSet();
  private List<Authorization> authorizations = Lists.newArrayList();
  private List<Parameter> parameters = Lists.newArrayList();
  private Set<ResponseMessage> responseMessages = Sets.newHashSet();
  private String deprecated;
  private boolean isHidden;

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

  public OperationBuilder produces(List<String> produces) {
    this.produces.addAll(produces);
    return this;
  }

  public OperationBuilder consumes(List<String> consumes) {
    this.consumes.addAll(consumes);
    return this;
  }

  public OperationBuilder protocol(List<String> protocol) {
    this.protocol.addAll(protocol);
    return this;
  }

  public OperationBuilder authorizations(List<Authorization> authorizations) {
    this.authorizations.addAll(authorizations);
    return this;
  }

  public OperationBuilder parameters(List<Parameter> parameters) {
    this.parameters.addAll(parameters);
    return this;
  }

  public OperationBuilder responseMessages(Set<ResponseMessage> responseMessages) {
    this.responseMessages = Sets.newHashSet(mergeResponseMessages(responseMessages));
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

  public Operation build() {
    return new Operation(method, summary, notes, responseClass, nickname, position, produces,
            consumes, protocol, authorizations, parameters, responseMessages, deprecated, isHidden);
  }

  private Set<ResponseMessage> mergeResponseMessages(Set<ResponseMessage> responseMessages) {
    //Add logic to consolidate the response messages
    ImmutableMap<Integer, ResponseMessage> responsesByCode = Maps.uniqueIndex(this.responseMessages, byStatusCode());
    Set<ResponseMessage> merged = Sets.newHashSet(this.responseMessages);
    for (ResponseMessage each : responseMessages) {
      if (responsesByCode.containsKey(each.getCode())) {
        ResponseMessage responseMessage = responsesByCode.get(each.getCode());
        String message = defaultIfAbsent(Strings.emptyToNull(responseMessage.getMessage()), HttpStatus.OK.getReasonPhrase());
        String responseWithModel = defaultIfAbsent(Strings.emptyToNull(responseMessage.getResponseModel()),
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