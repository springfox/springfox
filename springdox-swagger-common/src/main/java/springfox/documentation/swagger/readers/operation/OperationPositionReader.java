package springfox.documentation.swagger.readers.operation;

import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

@Component
public class OperationPositionReader implements OperationBuilderPlugin {

  private static final Logger log = LoggerFactory.getLogger(OperationPositionReader.class);

  @Override
  public void apply(OperationContext context) {
    ApiOperation apiOperation = context.getHandlerMethod().getMethodAnnotation(ApiOperation.class);
    if (null != apiOperation && apiOperation.position() > 0) {
      context.operationBuilder().position(apiOperation.position());
      log.debug("Added operation at position: {}", apiOperation.position());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
