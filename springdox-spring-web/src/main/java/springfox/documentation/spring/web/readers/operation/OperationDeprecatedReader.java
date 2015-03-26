package springfox.documentation.spring.web.readers.operation;

import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

@Component
public class OperationDeprecatedReader implements OperationBuilderPlugin {
  @Override
  public void apply(OperationContext context) {
    boolean isDeprecated = context.getHandlerMethod().getMethodAnnotation(Deprecated.class) != null;
    context.operationBuilder().deprecated(String.valueOf(isDeprecated));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
