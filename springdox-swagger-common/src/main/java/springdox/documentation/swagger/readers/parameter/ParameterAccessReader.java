package springdox.documentation.swagger.readers.parameter;

import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.ParameterBuilderPlugin;
import springdox.documentation.spi.service.contexts.ParameterContext;
import springdox.documentation.swagger.common.SwaggerPluginSupport;

import static com.google.common.base.Strings.*;

@Component("swaggerParameterAccessReader")
public class ParameterAccessReader implements ParameterBuilderPlugin {
  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
    if (apiParam != null && !isNullOrEmpty(apiParam.access())) {
      String access = apiParam.access();
      context.parameterBuilder().parameterAccess(access);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
