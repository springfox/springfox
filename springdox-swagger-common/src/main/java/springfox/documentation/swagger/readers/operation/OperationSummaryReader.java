package springfox.documentation.swagger.readers.operation;

import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

@Component
public class OperationSummaryReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {
    ApiOperation apiOperationAnnotation = context.getHandlerMethod().getMethodAnnotation(ApiOperation.class);
    if (null != apiOperationAnnotation && StringUtils.hasText(apiOperationAnnotation.value())) {
      context.operationBuilder().summary(apiOperationAnnotation.value());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
