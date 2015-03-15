package com.mangofactory.documentation.spring.web;

import com.mangofactory.documentation.RequestMappingEvaluator;
import com.mangofactory.documentation.RequestMappingPatternMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static java.lang.String.*;

public class SpringRequestMappingEvaluator implements RequestMappingEvaluator {
  private static final Logger log = LoggerFactory.getLogger(SpringRequestMappingEvaluator.class);
  private RequestMappingPatternMatcher requestMappingPatternMatcher;
  private List<Class<? extends Annotation>> excludeAnnotations = newArrayList();
  private Set<String> includePatterns = newHashSet();

  public SpringRequestMappingEvaluator(RequestMappingPatternMatcher requestMappingPatternMatcher) {
    this.requestMappingPatternMatcher = requestMappingPatternMatcher;
  }

  @Override
  public void appendExcludeAnnotations(Set<Class<? extends Annotation>> annotations) {
    excludeAnnotations.addAll(annotations);
  }

  @Override
  public void appendIncludePatterns(Set<String> includePatterns) {
    this.includePatterns.addAll(includePatterns);
  }

  @Override
  public boolean shouldIncludeRequestMapping(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
    return requestMappingMatchesAnIncludePattern(requestMappingInfo)
            && !classHasIgnoredAnnotatedRequestMapping(handlerMethod.getMethod().getDeclaringClass())
            && !hasIgnoredAnnotatedRequestMapping(handlerMethod);
  }

  @Override
  public boolean shouldIncludePath(String path) {
    boolean isMatch = requestMappingPatternMatcher.pathMatchesOneOfIncluded(path, includePatterns);
    if (isMatch) {
      return true;
    }
    log.info(format("Path did not match any include patterns: | %s", path));
    return false;
  }

  @Override
  public boolean classHasIgnoredAnnotatedRequestMapping(Class<?> handlerClass) {
    for (Class<? extends Annotation> annotation : excludeAnnotations) {
      if (handlerClass.isAnnotationPresent(annotation)) {
        log.info(format("Excluding method as its class is annotated with: %s", annotation));
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasIgnoredAnnotatedRequestMapping(HandlerMethod handlerMethod) {
    for (Class<? extends Annotation> annotation : excludeAnnotations) {
      if (null != AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation)) {
        log.info(format("Excluding method as it contains the excluded annotation: %s", annotation));
        return true;
      }
    }
    return false;
  }

  private boolean requestMappingMatchesAnIncludePattern(RequestMappingInfo requestMappingInfo) {

    PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
    return requestMappingPatternMatcher.patternConditionsMatchOneOfIncluded(patternsCondition, includePatterns);
  }


}
