/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

package springfox.documentation;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import springfox.documentation.annotations.Incubating;
import springfox.documentation.service.ResolvedMethodParameter;

import springfox.documentation.spring.wrapper.NameValueExpression;
import springfox.documentation.spring.wrapper.PatternsRequestCondition;
import springfox.documentation.spring.wrapper.RequestMappingInfo;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public interface RequestHandler extends Comparable<RequestHandler> {

  /**
   * @return declaring class
   * @deprecated @since 2.7.0 This is introduced to preserve backwards compat with groups
   */
  @Deprecated
  Class<?> declaringClass();

  boolean isAnnotatedWith(Class<? extends Annotation> annotation);

  PatternsRequestCondition getPatternsCondition();

  String groupName();

  String getName();

  Set<RequestMethod> supportedMethods();

  Set<MediaType> produces();

  Set<MediaType> consumes();

  Set<NameValueExpression<String>> headers();

  Set<NameValueExpression<String>> params();

  <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation);

  RequestHandlerKey key();

  List<ResolvedMethodParameter> getParameters();

  ResolvedType getReturnType();

  <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation);

  /**
   * @return request mapping info
   * @deprecated This is introduced to preserve backwards compat
   */
  @Deprecated
  RequestMappingInfo<?> getRequestMapping();

  /**
   * @return handler method
   * @deprecated This is introduced to preserve backwards compat
   */
  @Deprecated
  HandlerMethod getHandlerMethod();

  /**
   * This is to merge two request handlers that are indistinguishable other than the media types supported
   *
   * @param other handler
   * @return combined request handler
   * @since 2.5.0
   */
  @Incubating
  RequestHandler combine(RequestHandler other);

  @Override
  default int compareTo(RequestHandler other) {
    return byPatternsCondition()
        .thenComparing(byOperationName())
        .compare(this, other);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  static String sortedPaths(PatternsRequestCondition patternsCondition) {
    TreeSet<String> paths = new TreeSet<>(patternsCondition.getPatterns());
    return paths.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.joining(","));
  }

  static Comparator<RequestHandler> byPatternsCondition() {
    return Comparator.comparing(requestHandler -> sortedPaths(requestHandler.getPatternsCondition()));
  }

  static Comparator<RequestHandler> byOperationName() {
    return Comparator.comparing(RequestHandler::getName);
  }
}
