package springdox.documentation.spring.web.readers.operation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.OperationBuilderPlugin;
import springdox.documentation.spi.service.contexts.OperationContext;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DefaultOperationBuilder implements OperationBuilderPlugin {
  @Override
  public void apply(OperationContext context) {
    String operationName = context.getHandlerMethod().getMethod().getName();
    context.operationBuilder()
            .method(context.httpMethod())
            .nickname(operationName)
            .notes(operationName)
            .position(context.operationIndex())
            .summary(operationName);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
