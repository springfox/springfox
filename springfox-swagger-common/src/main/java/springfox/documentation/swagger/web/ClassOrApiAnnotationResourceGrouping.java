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

package springfox.documentation.swagger.web;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ResourceGroupingStrategy;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.*;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static org.springframework.core.annotation.AnnotationUtils.*;
import static org.springframework.util.StringUtils.*;
import static springfox.documentation.spring.web.paths.Paths.*;

@Component
@Deprecated
public class ClassOrApiAnnotationResourceGrouping implements ResourceGroupingStrategy {
  private static final Logger LOG = LoggerFactory.getLogger(ClassOrApiAnnotationResourceGrouping.class);

  @Override
  public String getResourceDescription(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
    Class<?> controllerClass = handlerMethod.getBeanType();
    String className = splitCamelCase(controllerClass.getSimpleName(), " ");

    return stripSlashes(extractAnnotation(controllerClass, descriptionOrValueExtractor())
              .filter(((Predicate<String>)String::isEmpty).negate())
              .orElse(className));
  }

  @Override
  public Integer getResourcePosition(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
    Class<?> controllerClass = handlerMethod.getBeanType();
    Api apiAnnotation = findAnnotation(controllerClass, Api.class);
    if (null != apiAnnotation && hasText(apiAnnotation.value())) {
      return apiAnnotation.position();
    }
    return 0;
  }

  @Override
  public Set<ResourceGroup> getResourceGroups(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
    return groups(handlerMethod).stream().map(toResourceGroup(requestMappingInfo, handlerMethod)).collect(toSet());
  }

  private Set<String> groups(HandlerMethod handlerMethod) {
    Class<?> controllerClass = handlerMethod.getBeanType();
    String group = splitCamelCase(controllerClass.getSimpleName(), " ");
    String apiValue = ofNullable(findAnnotation(controllerClass, Api.class))
        .map(toApiValue()).orElse("");
    return singleton(normalize(ofNullable(apiValue).filter(((Predicate<String>)String::isEmpty).negate()).orElse(group)));
  }

  private String normalize(String tag) {
    return tag.toLowerCase()
        .replaceAll(" ", "-")
        .replaceAll("/", "");
  }

  private Function<String, ResourceGroup> toResourceGroup(
      final RequestMappingInfo requestMappingInfo,
      final HandlerMethod handlerMethod) {

    return group -> {
      LOG.info("Group for method {} was {}", handlerMethod.getMethod().getName(), group);
      Integer position = getResourcePosition(requestMappingInfo, handlerMethod);
      return new ResourceGroup(group, handlerMethod.getBeanType(), position);
    };
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  private <T> T extractAnnotation(Class<?> controllerClass,
                                  Function<Api, T> annotationExtractor) {

    Api apiAnnotation = findAnnotation(controllerClass, Api.class);
    return annotationExtractor.apply(apiAnnotation);
  }

  private Function<Api, Optional<String>> descriptionOrValueExtractor() {
    return input -> {
      //noinspection ConstantConditions
      return descriptionExtractor().apply(input).map(Optional::of).orElse(valueExtractor().apply(input));
    };
  }

  private Function<Api, String> toApiValue() {
    return input -> normalize(input.value());
  }

  private Function<Api, Optional<String>> descriptionExtractor() {
    return input -> {
      if (null != input) {
        return of(input.description()).filter(((Predicate<String>)String::isEmpty).negate());
      }
      return empty();
    };
  }

  private Function<Api, Optional<String>> valueExtractor() {
    return input -> {
      if (null != input) {
        return of(input.value()).filter(((Predicate<String>)String::isEmpty).negate());
      }
      return empty();
    };
  }
}
