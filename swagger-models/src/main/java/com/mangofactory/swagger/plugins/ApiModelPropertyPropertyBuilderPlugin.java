package com.mangofactory.swagger.plugins;

import com.google.common.base.Optional;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.schema.plugins.ModelPropertyContext;
import com.mangofactory.schema.plugins.ModelPropertyBuilderPlugin;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;

import static com.mangofactory.schema.Annotations.*;
import static com.mangofactory.swagger.plugins.ApiModelProperties.*;

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
