package com.mangofactory.documentation.swagger.schema;

import com.google.common.base.Optional;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.schema.ModelPropertyBuilderPlugin;
import com.mangofactory.documentation.spi.schema.contexts.ModelPropertyContext;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;

import static com.mangofactory.documentation.schema.Annotations.*;
import static com.mangofactory.documentation.swagger.schema.ApiModelProperties.*;

@Component
public class ApiModelPropertyPropertyBuilderPlugin implements ModelPropertyBuilderPlugin {
  @Override
  public void apply(ModelPropertyContext context) {
    Optional<ApiModelProperty> annotation = Optional.absent();

    if (context.getAnnotatedElement().isPresent()) {
      annotation = annotation.or(findApiModePropertyAnnotation(context.getAnnotatedElement().get()));
    }
    if (context.getBeanPropertyDefinition().isPresent()) {
      annotation = annotation
              .or(findPropertyAnnotation(context.getBeanPropertyDefinition().get(), ApiModelProperty.class));
    }
    if (annotation.isPresent()) {
      context.getBuilder()
        .allowableValues(annotation.transform(toAllowableList()).orNull())
        .required(annotation.transform(toIsRequired()).or(false))
        .description(annotation.transform(toDescription()).orNull());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;//TODO
  }
}
