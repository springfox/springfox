package com.mangofactory.swagger.readers.operation;

import com.google.common.collect.Lists;
import com.mangofactory.swagger.readers.operation.parameter.ParameterAllowableReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.mangofactory.swagger.models.dto.Parameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class OperationImplicitParameterReader extends SwaggerParameterReader {

  @Override
  protected Collection<? extends Parameter> readParameters(RequestMappingContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    Method method = handlerMethod.getMethod();
    ApiImplicitParam annotation = AnnotationUtils.findAnnotation(method, ApiImplicitParam.class);
    List<Parameter> parameters = Lists.newArrayList();
    if (null != annotation) {
      parameters.add(OperationImplicitParameterReader.getImplicitParameter(annotation));
    }
    return parameters;
  }

  public static Parameter getImplicitParameter(ApiImplicitParam param) {
    return new Parameter(
            param.name(),
            param.value(),
            param.defaultValue(),
            param.required(),
            param.allowMultiple(),
            param.dataType(),
            ParameterAllowableReader.allowableValueFromString(param.allowableValues()),
            param.paramType(),
            param.access()
    );
  }

}

