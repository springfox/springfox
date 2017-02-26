/*
 *
 *  Copyright 2015 the original author or authors.
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

import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.DescriptionResolver;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterDefaultReader implements ParameterBuilderPlugin {
  private final DescriptionResolver descriptions;

  @Autowired
  public ParameterDefaultReader(DescriptionResolver descriptions) {
    this.descriptions = descriptions;
  }
  
  @Override
  public void apply(ParameterContext context) {
    String defaultValue = findAnnotatedDefaultValue(context.resolvedMethodParameter());
    boolean isSkip = ValueConstants.DEFAULT_NONE.equals(defaultValue);
    if (!isSkip) {
      context.parameterBuilder().defaultValue(defaultValue);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private String findAnnotatedDefaultValue(ResolvedMethodParameter methodParameter) {
    Optional<RequestParam> requestParam = methodParameter.findAnnotation(RequestParam.class);
    if (requestParam.isPresent()) {
      return descriptions.resolve(requestParam.get().defaultValue());
    }
    Optional<RequestHeader> requestHeader = methodParameter.findAnnotation(RequestHeader.class);
    if (requestHeader.isPresent()) {
      return descriptions.resolve(requestHeader.get().defaultValue());
    }
    return null;
  }
}
