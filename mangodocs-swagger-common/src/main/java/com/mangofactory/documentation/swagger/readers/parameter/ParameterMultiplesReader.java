package com.mangofactory.documentation.swagger.readers.parameter;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import static com.mangofactory.documentation.swagger.common.SwaggerPluginSupport.*;

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
    return pluginDoesApply(delimiter);
  }
}
