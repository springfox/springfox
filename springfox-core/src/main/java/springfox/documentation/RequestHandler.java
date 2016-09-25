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
import com.google.common.base.Optional;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.annotations.Incubating;
import springfox.documentation.service.ResolvedMethodParameter;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public interface RequestHandler {

  Class<?> declaringClass();

  boolean isAnnotatedWith(Class<? extends Annotation> annotation);

  PatternsRequestCondition getPatternsCondition();

  String groupName();

  String getName();

  Set<RequestMethod> supportedMethods();

  Set<? extends MediaType> produces();

  Set<? extends MediaType> consumes();

  Set<NameValueExpression<String>> headers();

  Set<NameValueExpression<String>> params();

  <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation);

  RequestHandlerKey key();

  List<ResolvedMethodParameter> getParameters();

  ResolvedType getReturnType();

  <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation);

  /**
   * @deprecated This is introduced to preserve backwards compat
   * @return
   */
  @Deprecated
  RequestMappingInfo getRequestMapping();

  /**
   * @deprecated This is introduced to preserve backwards compat
   * @return
   */
  @Deprecated
  HandlerMethod getHandlerMethod();

  /**
   * This is to merge two request handlers that are indistinguishable other than the media types supported
   * @param other handler
   * @since 2.5.0
   * @return
   */
  @Incubating
  RequestHandler combine(RequestHandler other);
}
