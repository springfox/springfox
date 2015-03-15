package springdox.documentation;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface RequestMappingEvaluator {
  void appendExcludeAnnotations(Set<Class<? extends Annotation>> annotations);

  void appendIncludePatterns(Set<String> includePatterns);

  boolean shouldIncludeRequestMapping(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);

  boolean shouldIncludePath(String path);

  boolean classHasIgnoredAnnotatedRequestMapping(Class<?> handlerClass);

  boolean hasIgnoredAnnotatedRequestMapping(HandlerMethod handlerMethod);
}
