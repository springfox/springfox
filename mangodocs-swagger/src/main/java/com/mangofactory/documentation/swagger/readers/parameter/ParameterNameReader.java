package com.mangofactory.documentation.swagger.readers.parameter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.*;

@Component("swaggerParameterNameReader")
public class ParameterNameReader implements ParameterBuilderPlugin {

  private ParameterAnnotationReader annotations = new ParameterAnnotationReader();

  public ParameterNameReader() {
  }

  @VisibleForTesting
  ParameterNameReader(ParameterAnnotationReader annotations) {
    this.annotations = annotations;
  }

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    Optional<ApiParam> apiParam = Optional.fromNullable(methodParameter.getParameterAnnotation(ApiParam.class))
            .or(annotations.fromHierarchy(methodParameter, ApiParam.class));

    if (apiParam.isPresent() && hasText(apiParam.get().name())) {
      context.parameterBuilder().name(apiParam.get().name());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

}
