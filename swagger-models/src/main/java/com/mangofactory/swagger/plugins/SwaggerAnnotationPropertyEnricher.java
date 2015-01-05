package com.mangofactory.swagger.plugins;

import com.google.common.base.Optional;
import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.documentation.plugins.ModelPropertyContext;
import com.mangofactory.documentation.plugins.ModelPropertyEnricher;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;

import static com.mangofactory.schema.Annotations.*;
import static com.mangofactory.schema.property.ApiModelProperties.*;

@Component
public class SwaggerAnnotationPropertyEnricher implements ModelPropertyEnricher {
  @Override
  public void enrich(ModelPropertyContext context) {
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
