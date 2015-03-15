package springdox.documentation.swagger.readers.operation;

import com.google.common.base.Splitter;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.OperationBuilderPlugin;
import springdox.documentation.spi.service.contexts.OperationContext;

import java.util.Set;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Sets.*;
import static org.springframework.core.annotation.AnnotationUtils.*;
import static springdox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
public class SwaggerMediaTypeReader implements OperationBuilderPlugin {
  @Override
  public void apply(OperationContext context) {
    ApiOperation annotation = findAnnotation(context.getHandlerMethod().getMethod(), ApiOperation.class);
    if (null != annotation) {
      context.operationBuilder().consumes(asSet(nullToEmpty(annotation.consumes())));
      context.operationBuilder().produces(asSet(nullToEmpty(annotation.produces())));
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }


  private Set<String> asSet(String mediaTypes) {
    return newHashSet(Splitter.on(',')
            .trimResults()
            .omitEmptyStrings()
            .splitToList(mediaTypes));
  }


}
