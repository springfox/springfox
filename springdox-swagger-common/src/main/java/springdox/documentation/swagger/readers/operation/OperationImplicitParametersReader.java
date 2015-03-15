package springdox.documentation.swagger.readers.operation;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springdox.documentation.service.Parameter;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.OperationBuilderPlugin;
import springdox.documentation.spi.service.contexts.OperationContext;

import java.lang.reflect.Method;
import java.util.List;

import static springdox.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
public class OperationImplicitParametersReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder().parameters(readParameters(context));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }

  protected List<Parameter> readParameters(OperationContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    Method method = handlerMethod.getMethod();
    ApiImplicitParams annotation = AnnotationUtils.findAnnotation(method, ApiImplicitParams.class);

    List<Parameter> parameters = Lists.newArrayList();
    if (null != annotation) {
      for (ApiImplicitParam param : annotation.value()) {
        parameters.add(OperationImplicitParameterReader.getImplicitParameter(param));
      }
    }

    return parameters;
  }
}
