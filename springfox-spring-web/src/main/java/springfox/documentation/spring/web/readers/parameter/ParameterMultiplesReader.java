package springfox.documentation.spring.web.readers.parameter;

import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterMultiplesReader implements ParameterBuilderPlugin {
  @Override
  public void apply(ParameterContext context) {

    MethodParameter methodParameter = context.methodParameter();
    Boolean allowMultiple;
    Class<?> parameterType = methodParameter.getParameterType();
    if (parameterType != null) {
      allowMultiple = parameterType.isArray()
              || Iterable.class.isAssignableFrom(parameterType);
      context.parameterBuilder().allowMultiple(allowMultiple);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
