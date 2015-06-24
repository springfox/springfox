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

package springfox.documentation.spring.web.readers.operation;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static springfox.documentation.builders.Parameters.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class OperationParameterRequestConditionReader implements OperationBuilderPlugin {

  private final TypeResolver resolver;

  @Autowired
  public OperationParameterRequestConditionReader(TypeResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void apply(OperationContext context) {
    ParamsRequestCondition paramsCondition = context.getRequestMappingInfo().getParamsCondition();
    List<Parameter> parameters = newArrayList();
    for (NameValueExpression<String> expression : paramsCondition.getExpressions()) {
      if (skipParameter(parameters, expression)) {
        continue;
      }

      String paramValue = expression.getValue();
      AllowableListValues allowableValues = null;
      if (!isNullOrEmpty(paramValue)) {
        allowableValues = new AllowableListValues(newArrayList(paramValue), "string");
      }
      Parameter parameter = new ParameterBuilder()
              .name(expression.getName())
              .description(null)
              .defaultValue(paramValue)
              .required(true)
              .allowMultiple(false)
              .type(resolver.resolve(String.class))
              .modelRef(new ModelRef("string"))
              .allowableValues(allowableValues)
              .parameterType("query")
              .build();
      parameters.add(parameter);
    }
    context.operationBuilder().parameters(parameters);
  }

  private boolean skipParameter(List<Parameter> parameters, NameValueExpression<String> expression) {
    return expression.isNegated() || parameterHandled(parameters, expression);
  }

  private boolean parameterHandled(List<Parameter> parameters, NameValueExpression<String> expression) {
    return any(parameters, withName(expression.getName()));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
