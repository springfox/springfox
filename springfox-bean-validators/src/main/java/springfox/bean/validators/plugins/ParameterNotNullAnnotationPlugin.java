/*
 *
 *  Copyright 2015-2017 the original author or authors.
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
package springfox.bean.validators.plugins;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;

@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class ParameterNotNullAnnotationPlugin implements ParameterBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(ParameterNotNullAnnotationPlugin.class);

  public static <T extends Annotation> Optional<T> validatorFromField(ParameterContext context, Class<T>
      annotationType) {

    MethodParameter methodParam = context.methodParameter();

    T annotatedElement = methodParam.getParameterAnnotation(annotationType);
    Optional<T> annotationValue = Optional.absent();
    if (annotatedElement != null) {
      annotationValue = Optional.fromNullable(annotatedElement);
    }
    return annotationValue;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ParameterContext context) {

    Optional<RequestParam> requestParam = extractRequestParamAnnotation(context);

    if (requestParam.isPresent()) {
      RequestParam requestParamValue = requestParam.get();

      if (requestParamValue.required()) {
        LOG.debug("@RequestParam.required=true: setting parameter as required");
        context.parameterBuilder().required(true);
      }

    } else {
      Optional<PathVariable> pathVariableParam = extractPathVariableAnnotation(context);
      if (pathVariableParam.isPresent()) {
        LOG.debug("@PathVariable present: setting parameter as required");
        context.parameterBuilder().required(true);

      } else {
        Optional<NotNull> notNull = extractAnnotation(context);

        if (notNull.isPresent()) {
          LOG.debug("@NotNull present: setting parameter as required");
          context.parameterBuilder().required(true);
        }
      }

    }

  }

  @VisibleForTesting
  Optional<NotNull> extractAnnotation(ParameterContext context) {
    return validatorFromField(context, NotNull.class);
  }

  @VisibleForTesting
  Optional<RequestParam> extractRequestParamAnnotation(ParameterContext context) {
    return validatorFromField(context, RequestParam.class);
  }

  @VisibleForTesting
  Optional<PathVariable> extractPathVariableAnnotation(ParameterContext context) {
    return validatorFromField(context, PathVariable.class);
  }

}
