package com.mangofactory.swagger.plugins;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.documentation.plugins.ModelEnricher;
import com.mangofactory.schema.ModelContext;
import com.wordnik.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Component
public class ApiModelEnricher implements ModelEnricher {
  private final TypeResolver typeResolver;

  @Autowired
  public ApiModelEnricher(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }


  @Override
  public void enrich(ModelContext context) {
    ApiModel annotation = AnnotationUtils.findAnnotation(forClass(context), ApiModel.class);
    if (annotation != null) {
      context.getBuilder()
              .description(annotation.description());
    }
  }

  private Class<?> forClass(ModelContext context) {
    return typeResolver.resolve(context.getType()).getErasedType();
  }


  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
