package springfox.documentation.swagger.readers.operation;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.lang.reflect.Method;
import java.util.List;

@Component
public class OperationImplicitParametersReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder().parameters(readParameters(context));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
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
