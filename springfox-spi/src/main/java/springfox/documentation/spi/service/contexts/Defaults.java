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

package springfox.documentation.spi.service.contexts;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.ClassSupport;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Response;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static springfox.documentation.schema.AlternateTypeRules.*;

@SuppressWarnings("deprecation")
public class Defaults {

  private HashSet<Class> ignored;
  private LinkedHashMap<RequestMethod, List<springfox.documentation.service.ResponseMessage>> responseMessages;
  private Map<HttpMethod, List<Response>> responses = new LinkedHashMap<>();
  private List<Class<? extends Annotation>> annotations;
  private Comparator<Operation> operationOrdering;
  private Comparator<ApiDescription> apiDescriptionOrdering;
  private Comparator<ApiListingReference> apiListingReferenceOrdering;

  public Defaults() {
    init();
  }

  public Set<Class> defaultIgnorableParameterTypes() {
    return ignored;
  }

  /**
   * Default response messages set on all api operations
   *
   * @return - map of method to response messages
   * @deprecated @since 3.0.0
   * Use {@link Defaults#defaultResponses()} instead
   */
  @Deprecated
  public Map<RequestMethod, List<springfox.documentation.service.ResponseMessage>> defaultResponseMessages() {
    return responseMessages;
  }

  /**
   * Default response messages set on all api operations
   *
   * @return - map of method to response messages
   */
  public Map<HttpMethod, List<Response>> defaultResponses() {
    return responses;
  }

  public List<Class<? extends Annotation>> defaultExcludeAnnotations() {
    return annotations;
  }

  public Comparator<Operation> operationOrdering() {
    return operationOrdering;
  }


  public Comparator<ApiDescription> apiDescriptionOrdering() {
    return apiDescriptionOrdering;
  }

  public Comparator<ApiListingReference> apiListingReferenceOrdering() {
    return apiListingReferenceOrdering;
  }

  public List<AlternateTypeRule> defaultRules(TypeResolver typeResolver) {
    List<AlternateTypeRule> rules = new ArrayList<>();
    rules.add(newRule(typeResolver.resolve(Map.class), typeResolver.resolve(Object.class)));
    rules.add(newRule(typeResolver.resolve(Map.class, String.class, Object.class),
        typeResolver.resolve(Object.class)));
    rules.add(newRule(typeResolver.resolve(Map.class, Object.class, Object.class),
        typeResolver.resolve(Object.class)));

    rules.add(newRule(typeResolver.resolve(ResponseEntity.class, WildcardType.class),
        typeResolver.resolve(WildcardType.class)));

    rules.add(newRule(typeResolver.resolve(HttpEntity.class, WildcardType.class),
        typeResolver.resolve(WildcardType.class)));
    rules.add(newRule(typeResolver.resolve(Optional.class, WildcardType.class), WildcardType.class));

    maybeAddRuleForClassName(typeResolver, rules, "java.util.Optional", WildcardType.class);

    maybeAddRuleForClassName(typeResolver, rules, "java.time.LocalDate", java.sql.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "java.time.LocalDateTime", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "java.time.Instant", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "java.time.OffsetDateTime", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "java.time.ZonedDateTime", java.util.Date.class);

    maybeAddRuleForClassName(typeResolver, rules, "org.threeten.bp.LocalDate", java.sql.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "org.threeten.bp.LocalDateTime", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "org.threeten.bp.Instant", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "org.threeten.bp.OffsetDateTime", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "org.threeten.bp.ZonedDateTime", java.util.Date.class);

    maybeAddRuleForClassName(typeResolver, rules, "org.joda.time.LocalDate", java.sql.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "org.joda.time.LocalDateTime", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "org.joda.time.Instant", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "org.joda.time.DateTime", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "org.joda.time.ReadableDateTime", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "org.joda.time.ReadableInstant", java.util.Date.class);
    maybeAddRuleForClassName(typeResolver, rules, "org.joda.time.DateMidnight", java.util.Date.class);

    return rules;
  }

  private void maybeAddRuleForClassName(
      TypeResolver typeResolver,
      List<AlternateTypeRule> rules,
      String className,
      Class<?> clazz) {
    Optional<Class<?>> fromClazz = ClassSupport.classByName(className);
    fromClazz.ifPresent(aClass -> rules.add(newRule(
        typeResolver.resolve(aClass),
        typeResolver.resolve(clazz))));
  }

  private void init() {
    initIgnorableTypes();
    initResponseMessages();
    initResponses();
    initExcludeAnnotations();
    initOrderings();
  }

  private void initResponses() {
    responses = new LinkedHashMap<>();
    responses.put(HttpMethod.GET, asList(
        new ResponseBuilder()
            .code(String.valueOf(OK.value()))
            .description(OK.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(NOT_FOUND.value()))
            .description(NOT_FOUND.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(FORBIDDEN.value()))
            .description(FORBIDDEN.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(UNAUTHORIZED.value()))
            .description(UNAUTHORIZED.getReasonPhrase())
            .build()));

    responses.put(HttpMethod.PUT, asList(
        new ResponseBuilder()
            .code(String.valueOf(CREATED.value()))
            .description(CREATED.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(NOT_FOUND.value()))
            .description(NOT_FOUND.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(FORBIDDEN.value()))
            .description(FORBIDDEN.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(UNAUTHORIZED.value()))
            .description(UNAUTHORIZED.getReasonPhrase())
            .build()));

    responses.put(HttpMethod.POST, asList(
        new ResponseBuilder()
            .code(String.valueOf(CREATED.value()))
            .description(CREATED.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(NOT_FOUND.value()))
            .description(NOT_FOUND.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(FORBIDDEN.value()))
            .description(FORBIDDEN.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(UNAUTHORIZED.value()))
            .description(UNAUTHORIZED.getReasonPhrase())
            .build()));

    responses.put(HttpMethod.DELETE, asList(
        new ResponseBuilder()
            .code(String.valueOf(NO_CONTENT.value()))
            .description(NO_CONTENT.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(FORBIDDEN.value()))
            .description(FORBIDDEN.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(UNAUTHORIZED.value()))
            .description(UNAUTHORIZED.getReasonPhrase())
            .build()));

    responses.put(HttpMethod.PATCH, asList(
        new ResponseBuilder()
            .code(String.valueOf(NO_CONTENT.value()))
            .description(NO_CONTENT.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(FORBIDDEN.value()))
            .description(FORBIDDEN.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(UNAUTHORIZED.value()))
            .description(UNAUTHORIZED.getReasonPhrase())
            .build()));

    responses.put(HttpMethod.TRACE, asList(
        new ResponseBuilder()
            .code(String.valueOf(NO_CONTENT.value()))
            .description(NO_CONTENT.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(FORBIDDEN.value()))
            .description(FORBIDDEN.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(UNAUTHORIZED.value()))
            .description(UNAUTHORIZED.getReasonPhrase())
            .build()));

    responses.put(HttpMethod.OPTIONS, asList(
        new ResponseBuilder()
            .code(String.valueOf(NO_CONTENT.value()))
            .description(NO_CONTENT.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(FORBIDDEN.value()))
            .description(FORBIDDEN.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(UNAUTHORIZED.value()))
            .description(UNAUTHORIZED.getReasonPhrase())
            .build()));

    responses.put(HttpMethod.HEAD, asList(
        new ResponseBuilder()
            .code(String.valueOf(NO_CONTENT.value()))
            .description(NO_CONTENT.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(FORBIDDEN.value()))
            .description(FORBIDDEN.getReasonPhrase())
            .build(),
        new ResponseBuilder()
            .code(String.valueOf(UNAUTHORIZED.value()))
            .description(UNAUTHORIZED.getReasonPhrase())
            .build()));
  }

  private void initOrderings() {
    operationOrdering = Orderings.positionComparator().thenComparing(Orderings.nickNameComparator());
    apiDescriptionOrdering = Orderings.apiPathCompatator();
    apiListingReferenceOrdering =
        Orderings.listingPositionComparator().thenComparing(Orderings.listingReferencePathComparator());
  }

  private void initExcludeAnnotations() {
    annotations = new ArrayList<>();
    annotations.add(ApiIgnore.class);
  }

  private void initIgnorableTypes() {
    ignored = new HashSet<>();
    ignored.add(Class.class);
    ignored.add(Void.class);
    ignored.add(Void.TYPE);
    ignored.add(HttpHeaders.class);
    ignored.add(BindingResult.class);
    ignored.add(UriComponentsBuilder.class);
    ignored.add(ApiIgnore.class); //Used to ignore parameters

    classFor("javax.servlet.ServletRequest").ifPresent(it -> ignored.add(it));
    classFor("javax.servlet.ServletResponse").ifPresent(it -> ignored.add(it));
    classFor("javax.servlet.http.HttpServletRequest").ifPresent(it -> ignored.add(it));
    classFor("javax.servlet.http.HttpServletResponse").ifPresent(it -> ignored.add(it));
    classFor("javax.servlet.ServletContext").ifPresent(it -> ignored.add(it));
  }

  Optional<Class> classFor(String className) {
    try {
      return Optional.of(Class.forName(className, false, this.getClass().getClassLoader()));
    } catch (ClassNotFoundException e) {
      return Optional.empty();
    }
  }

  private void initResponseMessages() {
    responseMessages = new LinkedHashMap<>();
    responseMessages.put(GET, asList(
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(OK.value())
            .message(OK.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(NOT_FOUND.value())
            .message(NOT_FOUND.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(FORBIDDEN.value())
            .message(FORBIDDEN.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(UNAUTHORIZED.value())
            .message(UNAUTHORIZED.getReasonPhrase())
            .responseModel(null).build()));

    responseMessages.put(PUT, asList(
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(CREATED.value())
            .message(CREATED.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(NOT_FOUND.value())
            .message(NOT_FOUND.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(FORBIDDEN.value())
            .message(FORBIDDEN.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(UNAUTHORIZED.value())
            .message(UNAUTHORIZED.getReasonPhrase())
            .responseModel(null).build()));

    responseMessages.put(POST, asList(
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(CREATED.value())
            .message(CREATED.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(NOT_FOUND.value())
            .message(NOT_FOUND.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(FORBIDDEN.value())
            .message(FORBIDDEN.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(UNAUTHORIZED.value())
            .message(UNAUTHORIZED.getReasonPhrase())
            .responseModel(null).build()));

    responseMessages.put(DELETE, asList(
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(NO_CONTENT.value())
            .message(NO_CONTENT.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(FORBIDDEN.value())
            .message(FORBIDDEN.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(UNAUTHORIZED.value())
            .message(UNAUTHORIZED.getReasonPhrase())
            .responseModel(null)
            .build()));

    responseMessages.put(PATCH, asList(
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(NO_CONTENT.value())
            .message(NO_CONTENT.getReasonPhrase())
            .responseModel(null).build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(FORBIDDEN.value())
            .message(FORBIDDEN.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(UNAUTHORIZED.value())
            .message(UNAUTHORIZED.getReasonPhrase())
            .responseModel(null)
            .build()));

    responseMessages.put(TRACE, asList(
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(NO_CONTENT.value())
            .message(NO_CONTENT.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(FORBIDDEN.value())
            .message(FORBIDDEN.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(UNAUTHORIZED.value())
            .message(UNAUTHORIZED.getReasonPhrase())
            .responseModel(null)
            .build()));

    responseMessages.put(OPTIONS, asList(
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(NO_CONTENT.value())
            .message(NO_CONTENT.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(FORBIDDEN.value())
            .message(FORBIDDEN.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(UNAUTHORIZED.value())
            .message(UNAUTHORIZED.getReasonPhrase())
            .responseModel(null)
            .build()));
    responseMessages.put(HEAD, asList(
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(NO_CONTENT.value())
            .message(NO_CONTENT.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(FORBIDDEN.value())
            .message(FORBIDDEN.getReasonPhrase())
            .responseModel(null)
            .build(),
        new springfox.documentation.builders.ResponseMessageBuilder()
            .code(UNAUTHORIZED.value())
            .message(UNAUTHORIZED.getReasonPhrase())
            .responseModel(null)
            .build()));
  }
}
