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

package springfox.documentation.swagger.readers.parameter;

import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ValueConstants;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.lang.annotation.Annotation;

import static com.google.common.base.Strings.*;
import static org.springframework.util.StringUtils.*;

@Component("swaggerParameterDefaultReader")
public class ParameterDefaultReader implements ParameterBuilderPlugin {
  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    String defaultValue = findAnnotatedDefaultValue(methodParameter);
    boolean isSkip = ValueConstants.DEFAULT_NONE.equals(defaultValue);
    if (!isSkip) {
      context.parameterBuilder().defaultValue(nullToEmpty(defaultValue));
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  private String findAnnotatedDefaultValue(MethodParameter methodParameter) {
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
    if (null != methodAnnotations) {
      for (Annotation annotation : methodAnnotations) {
        if (annotation instanceof ApiParam && hasText(((ApiParam) annotation).defaultValue())) {
          return ((ApiParam) annotation).defaultValue();
        }
      }
    }
    return null;
  }
}
