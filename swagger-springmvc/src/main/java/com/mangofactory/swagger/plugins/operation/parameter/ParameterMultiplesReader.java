package com.mangofactory.swagger.plugins.operation.parameter;

import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.springmvc.plugins.ParameterBuilderPlugin;
import com.mangofactory.springmvc.plugins.ParameterContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Component("swaggerParameterMultiplesReader")
public class ParameterMultiplesReader implements ParameterBuilderPlugin {
  @Override
  public void apply(ParameterContext context) {

    MethodParameter methodParameter = context.methodParameter();
    ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);

    Boolean allowMultiple;
    Class<?> parameterType = methodParameter.getParameterType();
    if (null != apiParam && !(parameterType != null
            && parameterType.isArray() && parameterType.getComponentType().isEnum())) {
      allowMultiple = apiParam.allowMultiple();
      context.parameterBuilder().allowMultiple(allowMultiple);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
