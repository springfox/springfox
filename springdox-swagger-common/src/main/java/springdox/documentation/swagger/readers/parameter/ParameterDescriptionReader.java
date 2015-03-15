package springdox.documentation.swagger.readers.parameter;

import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.ParameterBuilderPlugin;
import springdox.documentation.spi.service.contexts.ParameterContext;

import static org.springframework.util.StringUtils.*;
import static springdox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component("swaggerParameterDescriptionReader")
public class ParameterDescriptionReader implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
    String description = methodParameter.getParameterName();
    if (null != apiParam && hasText(apiParam.value())) {
      description = apiParam.value();
    }
    context.parameterBuilder().description(description);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }
}
