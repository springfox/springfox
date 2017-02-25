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

package springfox.documentation.spring.web.readers.parameter;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import static com.google.common.base.Strings.*;
import static java.lang.String.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterNameReader implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    String name = findParameterNameFromAnnotations(context.resolvedMethodParameter());
    if (isNullOrEmpty(name)) {
      Optional<String> discoveredName = context.resolvedMethodParameter().defaultName();
      name = discoveredName.isPresent()
             ? discoveredName.get()
             : format("param%s", context.resolvedMethodParameter().getParameterIndex());
    }
    context.parameterBuilder()
        .name(name)
        .description(name);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private String findParameterNameFromAnnotations(ResolvedMethodParameter methodParameter) {
    return methodParameter.findAnnotation(PathVariable.class).transform(pathVariableValue())
        .or(methodParameter.findAnnotation(ModelAttribute.class).transform(modelAttributeValue()))
        .or(methodParameter.findAnnotation(RequestParam.class).transform(requestParamValue()))
        .or(methodParameter.findAnnotation(RequestHeader.class).transform(requestHeaderValue()))
        .orNull();
  }


  private Function<RequestHeader, String> requestHeaderValue() {
    return new Function<RequestHeader, String>() {
      @Override
      public String apply(RequestHeader input) {
        return input.value();
      }
    };
  }

  private Function<RequestParam, String> requestParamValue() {
    return new Function<RequestParam, String>() {
      @Override
      public String apply(RequestParam input) {
        return input.value();
      }
    };
  }

  private Function<ModelAttribute, String> modelAttributeValue() {
    return new Function<ModelAttribute, String>() {
      @Override
      public String apply(ModelAttribute input) {
        return input.value();
      }
    };
  }

  private Function<PathVariable, String> pathVariableValue() {
    return new Function<PathVariable, String>() {
      @Override
      public String apply(PathVariable input) {
        return input.value();
      }
    };
  }

}
