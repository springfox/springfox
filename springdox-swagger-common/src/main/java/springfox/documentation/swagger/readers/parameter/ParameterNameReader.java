package springfox.documentation.swagger.readers.parameter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static org.springframework.util.StringUtils.*;

@Component("swaggerParameterNameReader")
public class ParameterNameReader implements ParameterBuilderPlugin {

  private ParameterAnnotationReader annotations = new ParameterAnnotationReader();

  public ParameterNameReader() {
  }

  @VisibleForTesting
  ParameterNameReader(ParameterAnnotationReader annotations) {
    this.annotations = annotations;
  }

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    Optional<ApiParam> apiParam = Optional.fromNullable(methodParameter.getParameterAnnotation(ApiParam.class))
            .or(annotations.fromHierarchy(methodParameter, ApiParam.class));

    if (apiParam.isPresent() && hasText(apiParam.get().name())) {
      context.parameterBuilder().name(apiParam.get().name());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

}
