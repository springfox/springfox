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

package springfox.documentation.spring.web.readers.parameter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ValueConstants;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.DescriptionResolver;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Strings.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterRequiredReader implements ParameterBuilderPlugin {
  private final DescriptionResolver descriptions;

  @Autowired
  public ParameterRequiredReader(DescriptionResolver descriptions) {
    this.descriptions = descriptions;
  }

  @Override
  public void apply(ParameterContext context) {
    ResolvedMethodParameter methodParameter = context.resolvedMethodParameter();
    context.parameterBuilder()
        .required(isRequired(context.getOperationContext(), methodParameter));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private boolean isRequired(
      OperationContext operationContext,
      ResolvedMethodParameter methodParameter) {

    Set<Boolean> requiredSet = new HashSet<Boolean>();

    // when the type is Optional, the required property of @RequestParam/@RequestHeader doesn't matter,
    // since the value is always a non-null Optional after conversion
    boolean optional = isOptional(methodParameter);

    Optional<RequestParam> requestParam = methodParameter.findAnnotation(RequestParam.class);
    if (requestParam.isPresent()) {
      requiredSet.add(!optional && isRequired(requestParam.get()));
    }

    Optional<RequestHeader> requestHeader = methodParameter.findAnnotation(RequestHeader.class);
    if (requestHeader.isPresent()) {
      requiredSet.add(!optional && requestHeader.get().required());
    }

    Optional<PathVariable> pathVariable = methodParameter.findAnnotation(PathVariable.class);
    if (pathVariable.isPresent()) {
      String paramName = MoreObjects.firstNonNull(
          emptyToNull(pathVariable.get().name()),
          methodParameter.defaultName().orNull());

      if (pathVariable.get().required() ||
          optionalButPresentInThePath(operationContext, pathVariable.get(), paramName)) {
        requiredSet.add(true);
      }
    }

    Optional<RequestBody> requestBody = methodParameter.findAnnotation(RequestBody.class);
    if (requestBody.isPresent()) {
      requiredSet.add(!optional && requestBody.get().required());
    }

    Optional<RequestPart> requestPart = methodParameter.findAnnotation(RequestPart.class);
    if (requestPart.isPresent()) {
      requiredSet.add(!optional && requestPart.get().required());
    }
    return requiredSet.contains(true);
  }

  private boolean optionalButPresentInThePath(
      OperationContext operationContext,
      PathVariable pathVariable,
      String paramName) {

    return !pathVariable.required()
         && operationContext.requestMappingPattern().contains("{" + paramName + "}");
  }

  @VisibleForTesting
  @SuppressWarnings("squid:S1872")
  boolean isOptional(ResolvedMethodParameter methodParameter) {
    return "java.util.Optional".equals(methodParameter.getParameterType().getErasedType().getName());
  }

  private boolean isRequired(RequestParam annotation) {
    String defaultValue = descriptions.resolve(annotation.defaultValue());
    boolean missingDefaultValue = ValueConstants.DEFAULT_NONE.equals(defaultValue);
    return annotation.required() && missingDefaultValue;
  }
}
