package springdox.documentation.swagger.readers.parameter;

import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.ParameterBuilderPlugin;
import springdox.documentation.spi.service.contexts.ParameterContext;

import java.lang.annotation.Annotation;

import static springdox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component("swaggerParameterRequiredReader")
public class ParameterRequiredReader implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    if(getAnnotatedRequired(methodParameter).isPresent()) {
      context.parameterBuilder().required(getAnnotatedRequired(methodParameter).get());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }

  private Optional<Boolean> getAnnotatedRequired(MethodParameter methodParameter) {
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();

    // when the type is Optional, the required property of @RequestParam/@RequestHeader doesn't matter,
    // since the value is always a non-null Optional after conversion

    if (null != methodAnnotations) {
      for (Annotation annotation : methodAnnotations) {
        if (annotation instanceof ApiParam) {
          return Optional.of(((ApiParam) annotation).required());
        }
      }
    }
    return Optional.absent();
  }
}
