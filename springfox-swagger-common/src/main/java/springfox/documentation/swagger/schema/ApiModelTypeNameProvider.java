package springfox.documentation.swagger.schema;

import com.wordnik.swagger.annotations.ApiModel;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.DefaultTypeNameProvider;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static com.google.common.base.Optional.*;
import static com.google.common.base.Strings.*;
import static org.springframework.core.annotation.AnnotationUtils.*;

@Component
public class ApiModelTypeNameProvider extends DefaultTypeNameProvider {
  @Override
  public String nameFor(Class<?> type) {
    ApiModel annotation = findAnnotation(type, ApiModel.class);
    String defaultTypeName = super.nameFor(type);
    if (annotation != null) {
      return fromNullable(emptyToNull(annotation.value())).or(defaultTypeName);
    }
    return defaultTypeName;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
