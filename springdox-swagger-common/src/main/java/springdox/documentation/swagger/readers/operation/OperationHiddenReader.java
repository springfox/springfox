package springdox.documentation.swagger.readers.operation;

import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.OperationBuilderPlugin;
import springdox.documentation.spi.service.contexts.OperationContext;
import springdox.documentation.swagger.common.SwaggerPluginSupport;

@Component
public class OperationHiddenReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {

    HandlerMethod handlerMethod = context.getHandlerMethod();
    ApiOperation methodAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);
    if (null != methodAnnotation) {
      context.operationBuilder().hidden(methodAnnotation.hidden());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
