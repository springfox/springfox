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
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.http.HttpMethod;
import springfox.documentation.OperationNameGenerator;
import springfox.documentation.annotations.Incubating;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.VendorExtension;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class OperationBuilder {
  private final OperationNameGenerator nameGenerator;
  private HttpMethod method = HttpMethod.GET;
  private String summary;
  private String notes;
  private String uniqueId;
  private String codeGenMethodNameStem;
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
  private ModelReference responseModel;
  private List<VendorExtension> vendorExtensions = newArrayList();

  public OperationBuilder(OperationNameGenerator nameGenerator) {
    this.nameGenerator = nameGenerator;
  }

  /**
   * Updates the http method
   *
   * @param method - http method, one of GET, POST, PUT etc.
   * @return this
   */
  public OperationBuilder method(HttpMethod method) {
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
   * Updates the uniqueId for the operation. This will be used to seed the unique id
   *
   * @param uniqueId - uniqueId for the operation
   * @return this
   */
  public OperationBuilder uniqueId(String uniqueId) {
    this.uniqueId = defaultIfAbsent(uniqueId, this.uniqueId);
    return this;
  }

  /**
   * This is an optional override that provides a custom method name stem, such that the method name
   * that is generated for the purposes of code-gen can be customized. However it must be kept in mind
   * that in-order the guarantee uniqueness of the name for code-gen the algoritm will still try to
   * append and indexer at the end of it e.g. someMethod_1, someMethod_2 etc. to preserve uniqueness in
   * the case there are duplicate names.
   *
   * @param codeGenMethodNameStem - provides a stem for the operation name as it will be used for code generation
   * @return this
   */
  @Incubating("2.3.0")
  public OperationBuilder codegenMethodNameStem(String codeGenMethodNameStem) {
    this.codeGenMethodNameStem = defaultIfAbsent(codeGenMethodNameStem, this.codeGenMethodNameStem);
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
   * @param parameters - input parameter definitions
   * @return
   */
  public OperationBuilder parameters(final List<Parameter> parameters) {
    List<Parameter> source = nullToEmptyList(parameters);
    List<Parameter> destination = newArrayList(this.parameters);
    ParameterMerger merger = new ParameterMerger(destination, source);
    this.parameters = newArrayList(merger.merged());
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
  public OperationBuilder responseModel(ModelReference responseType) {
    this.responseModel = defaultIfAbsent(responseType, this.responseModel);
    return this;
  }

  /**
   * Updates the tags that identify this operation
   *
   * @param tags - new set of tags
   * @return
   */
  public OperationBuilder tags(Set<String> tags) {
    this.tags = nullToEmptySet(tags);
    return this;
  }

  /**
   * Updates the operation extensions
   *
   * @param extensions - operation extensions
   * @return this
   */
  public OperationBuilder extensions(List<VendorExtension> extensions) {
    this.vendorExtensions.addAll(extensions);
    return this;
  }

  public Operation build() {
    String uniqueOperationId = nameGenerator.startingWith(uniqueOperationIdStem());
    return new Operation(
        method,
        summary,
        notes,
        responseModel,
        uniqueOperationId,
        position,
        tags,
        produces,
        consumes,
        protocol,
        securityReferences,
        parameters,
        responseMessages,
        deprecated,
        isHidden,
        vendorExtensions);
  }

  private String uniqueOperationIdStem() {
    String defaultStem = String.format("%sUsing%s", uniqueId, method);
    return Optional.fromNullable(emptyToNull(codeGenMethodNameStem)).or(defaultStem);
  }

  private Set<ResponseMessage> mergeResponseMessages(Set<ResponseMessage> responseMessages) {
    //Add logic to consolidate the response messages
    ImmutableMap<Integer, ResponseMessage> responsesByCode = Maps.uniqueIndex(this.responseMessages, byStatusCode());
    Set<ResponseMessage> merged = newHashSet(this.responseMessages);
    for (ResponseMessage each : responseMessages) {
      if (responsesByCode.containsKey(each.getCode())) {
        ResponseMessage responseMessage = responsesByCode.get(each.getCode());
        String message = defaultIfAbsent(emptyToNull(each.getMessage()), responseMessage.getMessage());
        ModelReference responseWithModel = defaultIfAbsent(each.getResponseModel(), responseMessage.getResponseModel());
        merged.remove(responseMessage);
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