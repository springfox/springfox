package springfox.documentation.swagger.readers.parameter;

import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Enums;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger.schema.ApiModelProperties;

import java.lang.annotation.Annotation;

import static com.google.common.base.Strings.*;

@Component("swaggerParameterAllowableReader")
public class ParameterAllowableReader implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    AllowableValues allowableValues = null;
    String allowableValueString = findAnnotatedAllowableValues(methodParameter);
    if (!isNullOrEmpty(allowableValueString)) {
      allowableValues = ApiModelProperties.allowableValueFromString(allowableValueString);
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
