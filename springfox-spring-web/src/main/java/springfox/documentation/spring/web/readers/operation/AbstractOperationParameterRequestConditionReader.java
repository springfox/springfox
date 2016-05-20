/*
 *
 *  Copyright 2016 the original author or authors.
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
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.Parameters.withName;

public abstract class AbstractOperationParameterRequestConditionReader implements OperationBuilderPlugin {

  private final TypeResolver resolver;

  public AbstractOperationParameterRequestConditionReader(TypeResolver resolver) {
    this.resolver = resolver;
  }

  public List<Parameter> getParameters(Set<NameValueExpression<String>> expressions, String parameterType) {
    List<Parameter> parameters = newArrayList();
    for (NameValueExpression<String> expression : expressions) {
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
              .parameterType(parameterType)
              .build();
      parameters.add(parameter);
    }

    return parameters;
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
