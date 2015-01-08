package com.mangofactory.swagger.readers;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.service.model.AllowableListValues;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.service.model.builder.ParameterBuilder;
import com.mangofactory.springmvc.plugins.OperationBuilderPlugin;
import com.mangofactory.springmvc.plugins.OperationContext;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;

import java.util.List;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

public class OperationParameterRequestConditionReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {
    ParamsRequestCondition paramsCondition = context.getRequestMappingInfo().getParamsCondition();
    List<Parameter> parameters = newArrayList();
    for (NameValueExpression<String> expression : paramsCondition.getExpressions()) {
      if (expression.isNegated() || any(nullToEmptyList(parameters),
              withName(expression.getName()))) {
        continue;
      }
      Parameter parameter = new ParameterBuilder()
              .name(expression.getName())
              .description(null)
              .defaultValue(expression.getValue())
              .required(true)
              .allowMultiple(false)
              .dataType("string")
              .allowableValues(new AllowableListValues(newArrayList(expression.getValue()), "string"))
              .parameterType("query")
              .parameterAccess("")
              .build();
      parameters.add(parameter);
    }
    context.operationBuilder().parameters(parameters);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private Iterable<Parameter> nullToEmptyList(List<Parameter> parameters) {
    if (parameters == null) {
      return newArrayList();
    }
    return parameters;
  }

  private Predicate<? super Parameter> withName(final String name) {
    return new Predicate<Parameter>() {
      @Override
      public boolean apply(Parameter input) {
        return Objects.equal(input.getName(), name);
      }
    };
  }
}
