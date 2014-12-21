package com.mangofactory.swagger.readers;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.mangofactory.swagger.models.servicemodel.AllowableListValues;
import com.mangofactory.swagger.models.servicemodel.Parameter;
import com.mangofactory.swagger.models.servicemodel.builder.ParameterBuilder;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;

import java.util.List;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

public class OperationParameterRequestConditionReader implements RequestMappingReader {
  @Override
  public void execute(RequestMappingContext context) {
    ParamsRequestCondition paramsCondition = context.getRequestMappingInfo().getParamsCondition();
    List<Parameter> parameters = (List<Parameter>) context.get("parameters");
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
