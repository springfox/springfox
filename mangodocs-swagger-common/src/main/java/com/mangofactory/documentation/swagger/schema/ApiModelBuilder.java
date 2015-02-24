package com.mangofactory.documentation.swagger.schema;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.schema.ModelBuilderPlugin;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import com.wordnik.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import static com.mangofactory.documentation.swagger.common.SwaggerPluginSupport.*;

@Component
public class ApiModelBuilder implements ModelBuilderPlugin {
  private final TypeResolver typeResolver;

  @Autowired
  public ApiModelBuilder(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }


  @Override
  public void apply(ModelContext context) {
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
    return pluginDoesApply(delimiter);
  }
}
