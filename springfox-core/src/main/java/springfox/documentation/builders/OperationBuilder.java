/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.builders;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.http.HttpStatus;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.builders.BuilderDefaults.*;

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
  private List<SecurityReference> securityReferences = newArrayList();
  private List<Parameter> parameters = newArrayList();
  private Set<ResponseMessage> responseMessages = newHashSet();
  private Set<String> tags = newHashSet();
  private String deprecated;
  private boolean isHidden;
  private ModelRef responseModel;

  /**
   * Updates the http method
   *
   * @param method - http method, one of GET, POST, PUT etc.
   * @return this
   */
  public OperationBuilder method(String method) {
    this.method = defaultIfAbsent(method, this.method);
    return this;
  }

  /**
   * Updates the operation summary
   *
   * @param summary - operation summary
   * @return this
   */
  public OperationBuilder summary(String summary) {
    this.summary = defaultIfAbsent(summary, this.summary);
    return this;
  }

  /**
   * Updates the operation notes
   *
   * @param notes - notes to describe the operaiton
   * @return
   */
  public OperationBuilder notes(String notes) {
    this.notes = defaultIfAbsent(notes, this.notes);
    return this;
  }

  /**
   * Updates the response class
   *
   * @param responseClass - string representation of the response class
   * @return
   */
  @Deprecated
  public OperationBuilder responseClass(String responseClass) {
    this.responseClass = defaultIfAbsent(responseClass, this.responseClass);
    return this;
  }

  /**
   * Updates the nickname for the operation
   *
   * @param nickname - nickname for the operation
   * @return this
   */
  public OperationBuilder nickname(String nickname) {
    this.nickname = defaultIfAbsent(nickname, this.nickname);
    return this;
  }

  /**
   * Updates the index of the operation
   *
   * @param position - position is used to sort the operation in a particular order
   * @return this
   */
  public OperationBuilder position(int position) {
    this.position = position;
    return this;
  }

  /**
   * Updates the existing media types with new entries that this documentation produces
   *
   * @param mediaTypes - new media types
   * @return this
   */
  public OperationBuilder produces(Set<String> mediaTypes) {
    this.produces.addAll(nullToEmptySet(mediaTypes));
    return this;
  }

  /**
   * Updates the existing media types with new entries that this documentation consumes
   *
   * @param mediaTypes - new media types
   * @return this
   */
  public OperationBuilder consumes(Set<String> mediaTypes) {
    this.consumes.addAll(nullToEmptySet(mediaTypes));
    return this;
  }

  /**
   * Update the protocols this operation supports
   *
   * @param protocols - protocols
   * @return this
   */
  public OperationBuilder protocols(Set<String> protocols) {
    this.protocol.addAll(nullToEmptySet(protocols));
    return this;
  }

  /**
   * Updates the security checks that apply to this operation
   *
   * @param securityReferences - authorization that reference security definitions
   * @return this
   */
  public OperationBuilder authorizations(List<SecurityReference> securityReferences) {
    this.securityReferences.addAll(nullToEmptyList(securityReferences));
    return this;
  }

  /**
   * Updates the input parameters this operation needs
   *
   * @param parameters - input parameter definitiosn
   * @return
   */
  public OperationBuilder parameters(List<Parameter> parameters) {
    this.parameters.addAll(nullToEmptyList(parameters));
    return this;
  }

  /**
   * Updates the response messages
   *
   * @param responseMessages - new response messages to be merged with existing response messages
   * @return this
   */
  public OperationBuilder responseMessages(Set<ResponseMessage> responseMessages) {
    this.responseMessages = newHashSet(mergeResponseMessages(responseMessages));
    return this;
  }

  /**
   * Marks the listing as deprecated
   *
   * @param deprecated - surely this had to be a boolean!! TODO!!
   * @return this
   */
  public OperationBuilder deprecated(String deprecated) {
    this.deprecated = defaultIfAbsent(deprecated, this.deprecated);
    return this;
  }

  /**
   * Marks the operation as hidden
   *
   * @param isHidden - boolean flag to indicate that the operation is hidden
   * @return this
   */
  public OperationBuilder hidden(boolean isHidden) {
    this.isHidden = isHidden;
    return this;
  }

  /**
   * Updates the reference to the response model
   *
   * @param responseType = response type model reference
   * @return this
   */
  public OperationBuilder responseModel(ModelRef responseType) {
    this.responseModel = defaultIfAbsent(responseType, this.responseModel);
    return this;
  }

  /**
   * Updates the tags that identify this operaiton
   *
   * @param tags - new set of tags
   * @return
   */
  public OperationBuilder tags(Set<String> tags) {
    this.tags.addAll(nullToEmptySet(tags));
    return this;
  }

  public Operation build() {
    return new Operation(method, summary, notes, responseModel, nickname, position, tags, produces,
        consumes, protocol, securityReferences, parameters, responseMessages, deprecated, isHidden);
  }

  private Set<ResponseMessage> mergeResponseMessages(Set<ResponseMessage> responseMessages) {
    //Add logic to consolidate the response messages
    ImmutableMap<Integer, ResponseMessage> responsesByCode = Maps.uniqueIndex(this.responseMessages, byStatusCode());
    Set<ResponseMessage> merged = newHashSet(this.responseMessages);
    for (ResponseMessage each : responseMessages) {
      if (responsesByCode.containsKey(each.getCode())) {
        ResponseMessage responseMessage = responsesByCode.get(each.getCode());
        String message = defaultIfAbsent(Strings.emptyToNull(responseMessage.getMessage()), HttpStatus.OK
            .getReasonPhrase());
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