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

package springfox.documentation.spi.service.contexts;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Ordering;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.Operation;
import springfox.documentation.service.ResponseMessage;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static springfox.documentation.schema.AlternateTypeRules.*;

public class Defaults {

  private HashSet<Class> ignored;
  private LinkedHashMap<RequestMethod, List<ResponseMessage>> responses;
  private List<Class<? extends Annotation>> annotations;
  private Ordering<Operation> operationOrdering;
  private Ordering<ApiDescription> apiDescriptionOrdering;
  private Ordering<ApiListingReference> apiListingReferenceOrdering;

  public Defaults() {
    init();
  }

  public Set<Class> defaultIgnorableParameterTypes() {
    return ignored;
  }

  /**
   * Default response messages set on all api operations
   */
  public Map<RequestMethod, List<ResponseMessage>> defaultResponseMessages() {
    return responses;
  }

  public List<Class<? extends Annotation>> defaultExcludeAnnotations() {
    return annotations;
  }

  public Ordering<Operation> operationOrdering() {
    return operationOrdering;
  }


  public Ordering<ApiDescription> apiDescriptionOrdering() {
    return apiDescriptionOrdering;
  }

  public Ordering<ApiListingReference> apiListingReferenceOrdering() {
    return apiListingReferenceOrdering;
  }

  public List<AlternateTypeRule> defaultRules(TypeResolver typeResolver) {
    List<AlternateTypeRule> rules = newArrayList();
    rules.add(newRule(typeResolver.resolve(Map.class), typeResolver.resolve(Object.class)));
    rules.add(newRule(typeResolver.resolve(Map.class, String.class, Object.class),
            typeResolver.resolve(Object.class)));
    rules.add(newRule(typeResolver.resolve(Map.class, Object.class, Object.class),
            typeResolver.resolve(Object.class)));


    rules.add(newRule(typeResolver.resolve(ResponseEntity.class, WildcardType.class),
            typeResolver.resolve(WildcardType.class)));

    rules.add(newRule(typeResolver.resolve(HttpEntity.class, WildcardType.class),
            typeResolver.resolve(WildcardType.class)));
//    rules.add(twoDimensionalArrayRule(typeResolver));
    return rules;
  }



  private void init() {
    initIgnorableTypes();
    initResponseMessages();
    initExcludeAnnotations();
    initOrderings();
  }

  private void initOrderings() {
    operationOrdering = Ordering.from(Orderings.positionComparator()).compound(Orderings.nickNameComparator());
    apiDescriptionOrdering = Ordering.from(Orderings.apiPathCompatator());
    apiListingReferenceOrdering = Ordering.from(Orderings.listingPositionComparator()).compound(Orderings.listingReferencePathComparator());
  }

  private void initExcludeAnnotations() {
    annotations = new ArrayList<Class<? extends Annotation>>();
    annotations.add(ApiIgnore.class);
  }

  private void initIgnorableTypes() {
    ignored = newHashSet();
    ignored.add(ServletRequest.class);
    ignored.add(Class.class);
    ignored.add(Void.class);
    ignored.add(Void.TYPE);
    ignored.add(HttpHeaders.class);
    ignored.add(ServletResponse.class);
    ignored.add(HttpServletRequest.class);
    ignored.add(HttpServletResponse.class);
    ignored.add(HttpHeaders.class);
    ignored.add(BindingResult.class);
    ignored.add(ServletContext.class);
    ignored.add(UriComponentsBuilder.class);
    ignored.add(ApiIgnore.class);
  }

  private void initResponseMessages() {
    responses = newLinkedHashMap();
    responses.put(GET, asList(
            new ResponseMessageBuilder()
                    .code(OK.value())
                    .message(OK.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(NOT_FOUND.value())
                    .message(NOT_FOUND.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null).build()));

    responses.put(PUT, asList(
            new ResponseMessageBuilder()
                    .code(CREATED.value())
                    .message(CREATED.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(NOT_FOUND.value())
                    .message(NOT_FOUND.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null).build()));

    responses.put(POST, asList(
            new ResponseMessageBuilder()
                    .code(CREATED.value())
                    .message(CREATED.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(NOT_FOUND.value())
                    .message(NOT_FOUND.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null).build()));

    responses.put(DELETE, asList(
            new ResponseMessageBuilder()
                    .code(NO_CONTENT.value())
                    .message(NO_CONTENT.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null)
                    .build()));

    responses.put(PATCH, asList(
            new ResponseMessageBuilder()
                    .code(NO_CONTENT.value())
                    .message(NO_CONTENT.getReasonPhrase())
                    .responseModel(null).build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null)
                    .build()));

    responses.put(TRACE, asList(
            new ResponseMessageBuilder()
                    .code(NO_CONTENT.value())
                    .message(NO_CONTENT.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null)
                    .build()));

    responses.put(OPTIONS, asList(
            new ResponseMessageBuilder()
                    .code(NO_CONTENT.value())
                    .message(NO_CONTENT.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null)
                    .build()));
    responses.put(HEAD, asList(
            new ResponseMessageBuilder()
                    .code(NO_CONTENT.value())
                    .message(NO_CONTENT.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(FORBIDDEN.value())
                    .message(FORBIDDEN.getReasonPhrase())
                    .responseModel(null)
                    .build(),
            new ResponseMessageBuilder()
                    .code(UNAUTHORIZED.value())
                    .message(UNAUTHORIZED.getReasonPhrase())
                    .responseModel(null)
                    .build()));
  }
}
