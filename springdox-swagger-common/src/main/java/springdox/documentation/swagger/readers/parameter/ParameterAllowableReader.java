package springdox.documentation.swagger.readers.parameter;

import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import springdox.documentation.schema.Enums;
import springdox.documentation.service.AllowableValues;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.ParameterBuilderPlugin;
import springdox.documentation.spi.service.contexts.ParameterContext;
import springdox.documentation.swagger.common.SwaggerPluginSupport;

import java.lang.annotation.Annotation;

import static com.google.common.base.Strings.*;
import static springdox.documentation.swagger.schema.ApiModelProperties.*;

@Component("swaggerParameterAllowableReader")
public class ParameterAllowableReader implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    AllowableValues allowableValues = null;
    String allowableValueString = findAnnotatedAllowableValues(methodParameter);
    if (!isNullOrEmpty(allowableValueString)) {
      allowableValues = allowableValueFromString(allowableValueString);
    } else {
      if (methodParameter.getParameterType().isEnum()) {
        allowableValues = Enums.allowableValues(methodParameter.getParameterType());
      }
      if (methodParameter.getParameterType().isArray()) {
        allowableValues = Enums.allowableValues(methodParameter.getParameterType().getComponentType());
      }
    }
    context.parameterBuilder().allowableValues(allowableValues);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  private String findAnnotatedAllowableValues(MethodParameter methodParameter) {
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
    if (null != methodAnnotations) {
      for (Annotation annotation : methodAnnotations) {
        if (annotation instanceof ApiParam) {
          return ((ApiParam) annotation).allowableValues();
        }
      }
    }
    return null;
  }
}
