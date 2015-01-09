package com.mangofactory.swagger.web;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.spring.web.ResourceGroupingStrategy;
import com.mangofactory.spring.web.scanners.ResourceGroup;
import com.wordnik.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.spring.web.Paths.*;
import static org.springframework.util.StringUtils.*;

@Component
public class ClassOrApiAnnotationResourceGrouping implements ResourceGroupingStrategy {
  private static final Logger LOG = LoggerFactory.getLogger(ClassOrApiAnnotationResourceGrouping.class);

  public ClassOrApiAnnotationResourceGrouping() {
  }

  @Override
  public String getResourceDescription(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
    Class<?> controllerClass = handlerMethod.getBeanType();
    String group = splitCamelCase(controllerClass.getSimpleName(), " ");
    return extractAnnotation(controllerClass, descriptionOrValueExtractor()).or(group);
  }

  @Override
  public Integer getResourcePosition(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
    Class<?> controllerClass = handlerMethod.getBeanType();
    Api apiAnnotation = AnnotationUtils.findAnnotation(controllerClass, Api.class);
    if (null != apiAnnotation && hasText(apiAnnotation.value())) {
      return apiAnnotation.position();
    }
    return 0;
  }

  @Override
  public Set<ResourceGroup> getResourceGroups(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
    String group = getClassOrApiAnnotationValue(handlerMethod).toLowerCase()
            .replaceAll(" ", "-")
            .replaceAll("/", "");
    LOG.info("Group for method {} was {}", handlerMethod.getMethod().getName(), group);
    Integer position = getResourcePosition(requestMappingInfo, handlerMethod);
    return newHashSet(new ResourceGroup(group.toLowerCase(), handlerMethod.getBeanType(), position));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private String getClassOrApiAnnotationValue(HandlerMethod handlerMethod) {
    Class<?> controllerClass = handlerMethod.getBeanType();
    String group = splitCamelCase(controllerClass.getSimpleName(), " ");

    return extractAnnotation(controllerClass, valueExtractor()).or(group);
  }

  private Optional<String> extractAnnotation(Class<?> controllerClass,
      Function<Api, Optional<String>> annotationExtractor) {

    Api apiAnnotation = AnnotationUtils.findAnnotation(controllerClass, Api.class);
    return annotationExtractor.apply(apiAnnotation);
  }

  private Function<Api, Optional<String>> descriptionOrValueExtractor() {
    return new Function<Api, Optional<String>>() {
      @Override
      public Optional<String> apply(Api input) {
        return descriptionExtractor().apply(input).or(valueExtractor().apply(input));
      }
    };
  }

  private Function<Api, Optional<String>> valueExtractor() {
    return new Function<Api, Optional<String>>() {
      @Override
      public Optional<String> apply(Api input) {
        if (null != input) {
          String stripSlashes = input.value().replace("/", "");
          return Optional.fromNullable(emptyToNull(stripSlashes));
        }
        return Optional.absent();
      }
    };
  }

  private Function<Api, Optional<String>> descriptionExtractor() {
    return new Function<Api, Optional<String>>() {
      @Override
      public Optional<String> apply(Api input) {
        if (null != input) {
          return Optional.fromNullable(emptyToNull(input.description()));
        }
        return Optional.absent();
      }
    };
  }
}
