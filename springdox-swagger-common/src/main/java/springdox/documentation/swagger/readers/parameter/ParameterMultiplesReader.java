package springdox.documentation.swagger.readers.parameter;

import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.ParameterBuilderPlugin;
import springdox.documentation.spi.service.contexts.ParameterContext;

import static springdox.documentation.swagger.common.SwaggerPluginSupport.*;

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
