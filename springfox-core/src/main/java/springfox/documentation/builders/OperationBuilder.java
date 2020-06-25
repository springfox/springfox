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
package springfox.documentation.builders;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import springfox.documentation.OperationNameGenerator;
import springfox.documentation.annotations.Incubating;
import springfox.documentation.common.ExternalDocumentation;
import springfox.documentation.schema.Example;
import springfox.documentation.service.Operation;
import springfox.documentation.service.RequestBody;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.Response;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.Optional.*;
import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.of;
import static org.springframework.http.MediaType.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class OperationBuilder {
  private static final Collection<String> REQUEST_BODY_MEDIA_TYPES
      = of(APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE).collect(toSet());
  private final OperationNameGenerator nameGenerator;
  private final Set<String> tags = new TreeSet<>();
  private final List<SecurityReference> securityReferences = new ArrayList<>();
  private final MultiValueMap<String, Response> responses = new LinkedMultiValueMap<>();
  private final Set<RequestParameter> requestParameters = new HashSet<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

  private HttpMethod method = HttpMethod.GET;
  private String summary;
  private String notes;
  private String uniqueId;
  private String codeGenMethodNameStem;
  private String deprecated;
  private RequestBody body;
  private ExternalDocumentation externalDocumentation;

  //TODO: to be deprecated
  private boolean isHidden;
  @SuppressWarnings("deprecation")
  private springfox.documentation.schema.ModelReference responseModel;
  private int position;
  @SuppressWarnings("deprecation")
  private final List<springfox.documentation.service.Parameter> parameters = new ArrayList<>();
  @SuppressWarnings("deprecation")
  private final Set<springfox.documentation.service.ResponseMessage> responseMessages = new HashSet<>();
  private final Set<String> produces = new TreeSet<>();
  private final Set<String> consumes = new TreeSet<>();
  private final Set<String> protocol = new TreeSet<>();

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
   * @param notes - notes to describe the operation
   * @return this
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
   * that in-order the guarantee uniqueness of the name for code-gen the algorithm will still try to
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
  public OperationBuilder authorizations(Collection<SecurityReference> securityReferences) {
    List<SecurityReference> newSecurityReferences = nullToEmptyList(securityReferences);
    if (!newSecurityReferences.isEmpty()) {
      this.securityReferences.clear();
      this.securityReferences.addAll(newSecurityReferences);
    }
    return this;
  }

  /**
   * Updates the input parameters this operation needs
   *
   * @param parameters - input parameter definitions
   * @return this
   * @deprecated @since 3.0.0
   * Use @see {@link OperationBuilder#requestParameters(Collection)}
   */
  @Deprecated
  public OperationBuilder parameters(List<springfox.documentation.service.Parameter> parameters) {
    List<springfox.documentation.service.Parameter> source = nullToEmptyList(parameters);
    List<springfox.documentation.service.Parameter> destination = new ArrayList<>(this.parameters);
    ParameterMerger merger = new ParameterMerger(destination, source);
    this.parameters.clear();
    this.parameters.addAll(merger.merged());
    return this;
  }


  /**
   * @param responseMessages - new response messages to be merged with existing response messages
   * @return this
   * @deprecated @since 3.0.0
   * Updates the response messages
   * Use @see {@link OperationBuilder#responses(Collection)}
   */
  @Deprecated
  public OperationBuilder responseMessages(Set<springfox.documentation.service.ResponseMessage> responseMessages) {
    Set<springfox.documentation.service.ResponseMessage> merged = mergeResponseMessages(responseMessages);
    this.responseMessages.clear();
    this.responseMessages.addAll(merged);
    return this;
  }

  /**
   * Updates the response messages
   *
   * @param responses - new response messages to be merged with existing response messages
   * @return this
   * @since 3.0.0
   */
  //TODO: needs to change
  public OperationBuilder responses(Collection<Response> responses) {
    responses.forEach(r -> this.responses.add(r.getCode(), r));
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

  public OperationBuilder externalDocumentation(ExternalDocumentation externalDocumentation) {
    this.externalDocumentation = externalDocumentation;
    return this;
  }


  /**
   * @param responseType = response type model reference
   * @return this
   * @deprecated @since 3.0.0
   * Updates the reference to the response model
   * Use @see {@link OperationBuilder#responses(Collection)}
   */
  @Deprecated
  public OperationBuilder responseModel(springfox.documentation.schema.ModelReference responseType) {
    this.responseModel = defaultIfAbsent(responseType, this.responseModel);
    return this;
  }

  /**
   * Updates the tags that identify this operation
   *
   * @param tags - new set of tags
   * @return this
   */
  public OperationBuilder tags(Set<String> tags) {
    Set<String> newTags = nullToEmptySet(tags);
    if (!newTags.isEmpty()) {
      this.tags.clear();
      this.tags.addAll(newTags);
    }
    return this;
  }

  /**
   * Updates the operation extensions
   *
   * @param extensions - operation extensions
   * @return this
   */
  public OperationBuilder extensions(List<VendorExtension> extensions) {
    this.vendorExtensions.addAll(nullToEmptyList(extensions));
    return this;
  }


  /**
   * Updates the operation request body. This overrides the one determined by the plugins
   *
   * @param requestBody - operation extensions
   * @return this
   * @since 3.0.0
   */
  public OperationBuilder requestBody(RequestBody requestBody) {
    this.body = requestBody;
    return this;
  }

  /**
   * Updates the operation response body
   *
   * @param parameters - operation extensions
   * @return this
   * @since 3.0.0
   */
  public OperationBuilder requestParameters(Collection<RequestParameter> parameters) {
    RequestParameterMerger merger = new RequestParameterMerger(
        this.requestParameters,
        nullToEmptyList(parameters));
    this.requestParameters.clear();
    this.requestParameters.addAll(merger.merge());
    return this;
  }


  public Operation build() {
    String uniqueOperationId = nameGenerator.startingWith(uniqueOperationIdStem());

    Set<Response> responses = new HashSet<>();
    for (String code : this.responses.keySet()) {
      if (this.responses.get(code).size() > 0) {
        responses.add(mergeResponses(this.responses.get(code)));
      }
    }
    SortedSet<RequestParameter> requestParameters = new TreeSet<>(defaultRequestParameterComparator());
    requestParameters.addAll(this.requestParameters);

    return new Operation(
        method,
        summary,
        notes,
        externalDocumentation,
        responseModel,
        uniqueOperationId,
        position,
        tags,
        produces,
        adjustConsumableMediaTypes(),
        protocol,
        securityReferences,
        parameters,
        responseMessages,
        deprecated,
        isHidden,
        vendorExtensions,
        requestParameters,
        body,
        responses);
  }

  private Response mergeResponses(List<Response> responses) {
    if (responses.size() == 1) {
      return responses.get(0);
    }
    Response response = null;
    for (Response each : responses) {
      response = new ResponseBuilder()
          .copyOf(response)
          .copyOf(each)
          .build();
    }
    return response;
  }

  private Set<String> adjustConsumableMediaTypes() {
    Set<String> adjustedConsumes = new HashSet<>(consumes);
    if (of(HttpMethod.GET, HttpMethod.DELETE).anyMatch(Predicate.isEqual(method))) {
      adjustedConsumes.removeAll(REQUEST_BODY_MEDIA_TYPES);
    }
    return adjustedConsumes;
  }

  private String uniqueOperationIdStem() {
    String defaultStem = String.format("%sUsing%s", uniqueId, method);
    return ofNullable(codeGenMethodNameStem).filter(((Predicate<String>) String::isEmpty).negate())
        .orElse(defaultStem);
  }

  @Deprecated
  private Set<springfox.documentation.service.ResponseMessage> mergeResponseMessages(
      Set<springfox.documentation.service.ResponseMessage> responseMessages) {
    //Add logic to consolidate the response messages
    Map<Integer, springfox.documentation.service.ResponseMessage> responsesByCode = this.responseMessages.stream()
        .collect(toMap(springfox.documentation.service.ResponseMessage::getCode, identity()));
    Set<springfox.documentation.service.ResponseMessage> merged = new HashSet<>(this.responseMessages);
    for (springfox.documentation.service.ResponseMessage each : responseMessages) {
      if (responsesByCode.containsKey(each.getCode())) {
        springfox.documentation.service.ResponseMessage responseMessage = responsesByCode.get(each.getCode());
        String message = defaultIfAbsent(ofNullable(each.getMessage())
            .filter(((Predicate<String>) String::isEmpty).negate())
            .orElse(null), responseMessage.getMessage());
        List<Example> examples = Stream.concat(
            ofNullable(responseMessage.getExamples()).orElse(emptyList())
                .stream(),
            ofNullable(each.getExamples()).orElse(emptyList())
                .stream())
            .collect(toList());
        springfox.documentation.schema.ModelReference responseWithModel = defaultIfAbsent(each.getResponseModel(),
            responseMessage.getResponseModel());
        merged.remove(responseMessage);
        merged.add(new ResponseMessageBuilder()
            .code(each.getCode())
            .message(message)
            .responseModel(responseWithModel)
            .examples(examples)
            .headersWithDescription(responseMessage.getHeaders())
            .headersWithDescription(each.getHeaders())
            .build());
      } else {
        merged.add(each);
      }
    }
    return merged;
  }

  private Comparator<RequestParameter> defaultRequestParameterComparator() {
    return (p1, p2) -> nullToEmpty(p1.getName()).compareToIgnoreCase(nullToEmpty(p2.getName()));
  }
}