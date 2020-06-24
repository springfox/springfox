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


import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.util.Optional;

import static java.lang.String.*;
import static org.springframework.util.StringUtils.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings("deprecation")
public class ParameterNameReader implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    String name = findParameterNameFromAnnotations(context.resolvedMethodParameter());
    if (isEmpty(name)) {
      Optional<String> discoveredName = context.resolvedMethodParameter().defaultName();
      name = discoveredName
          .orElseGet(() -> format("param%s", context.resolvedMethodParameter().getParameterIndex()));
    }
    context.parameterBuilder()
        .name(name)
        .description(name);
    context.requestParameterBuilder()
        .name(name)
        .description(name);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private String findParameterNameFromAnnotations(ResolvedMethodParameter methodParameter) {
    return methodParameter.findAnnotation(PathVariable.class).map(PathVariable::value)
        .orElse(methodParameter.findAnnotation(ModelAttribute.class).map(ModelAttribute::value)
        .orElse(methodParameter.findAnnotation(RequestParam.class).map(RequestParam::value)
        .orElse(methodParameter.findAnnotation(RequestHeader.class).map(RequestHeader::value)
        .orElse(methodParameter.findAnnotation(RequestPart.class).map(RequestPart::value)
        .orElse(null)))));
  }

}
