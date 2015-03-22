package springdox.documentation.swagger.schema;

import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;
import springdox.documentation.schema.Annotations;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springdox.documentation.spi.schema.contexts.ModelPropertyContext;

import static springdox.documentation.swagger.common.SwaggerPluginSupport.*;
import static springdox.documentation.swagger.schema.ApiModelProperties.*;

@Component
public class ApiModelPropertyPropertyBuilder implements ModelPropertyBuilderPlugin {
  @Override
  public void apply(ModelPropertyContext context) {
    Optional<ApiModelProperty> annotation = Optional.absent();

    if (context.getAnnotatedElement().isPresent()) {
      annotation = annotation.or(ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get()));
    }
    if (context.getBeanPropertyDefinition().isPresent()) {
      annotation = annotation
              .or(Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), ApiModelProperty.class));
    }
    if (annotation.isPresent()) {
      context.getBuilder()
        .allowableValues(annotation.transform(toAllowableValues()).orNull())
        .required(annotation.transform(ApiModelProperties.toIsRequired()).or(false))
        .description(annotation.transform(ApiModelProperties.toDescription()).orNull());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }
}
