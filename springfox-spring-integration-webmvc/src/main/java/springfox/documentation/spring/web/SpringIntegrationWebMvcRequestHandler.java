/*
 *
 *  Copyright 2016-2017 the original author or authors.
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
package springfox.documentation.spring.web;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.integration.http.inbound.BaseHttpInboundEndpoint;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.web.plugins.SpringIntegrationParametersProvider;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;

/**
 * Provides information about WebMvc based Spring Integration inbound HTTP handlers.
 */
public class SpringIntegrationWebMvcRequestHandler extends WebMvcRequestHandler {
  private final HandlerMethodResolver methodResolver;
  private final HandlerMethod handlerMethod;
  private SpringIntegrationParametersProvider parametersProvider;

  public SpringIntegrationWebMvcRequestHandler(
      String contextPath,
      HandlerMethodResolver methodResolver,
      RequestMappingInfo requestMapping,
      HandlerMethod handlerMethod,
      SpringIntegrationParametersProvider parametersProvider) {
    super(contextPath, methodResolver, requestMapping, handlerMethod);

    this.methodResolver = methodResolver;
    this.handlerMethod = handlerMethod;
    this.parametersProvider = parametersProvider;
  }

  @Override
  public String groupName() {
    // TODO come up with a better group name, instead of generic handler class name
    //   maybe the defining class of a flow in DSL? - but what about xml?
    return ControllerNamingUtils.controllerNameAsGroup(handlerMethod);
  }

  @Override
  public String getName() {
    return ((BaseHttpInboundEndpoint) handlerMethod.getBean())
        .getComponentName();
  }

  @Override
  public ResolvedType getReturnType() {
    // always void, hence we need the spring restdocs plugin
    return methodResolver.methodReturnType(handlerMethod);
  }

  @Override
  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    // TODO see if we can synthesize the annotations usually requested here
    return ofNullable(AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation));
  }

  @Override
  public List<ResolvedMethodParameter> getParameters() {

    BaseHttpInboundEndpoint inboundEndpoint = (BaseHttpInboundEndpoint) handlerMethod.getBean();
    return parametersProvider.getParameters(inboundEndpoint);
  }

}
