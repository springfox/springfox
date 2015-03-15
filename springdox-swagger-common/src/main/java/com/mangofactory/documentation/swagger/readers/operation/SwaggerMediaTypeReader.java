package com.mangofactory.documentation.swagger.readers.operation;

import com.google.common.base.Splitter;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.documentation.swagger.common.SwaggerPluginSupport.*;
import static org.springframework.core.annotation.AnnotationUtils.*;

@Component
public class SwaggerMediaTypeReader implements OperationBuilderPlugin {
  @Override
  public void apply(OperationContext context) {
    ApiOperation annotation = findAnnotation(context.getHandlerMethod().getMethod(), ApiOperation.class);
    if (null != annotation) {
      context.operationBuilder().consumes(asSet(nullToEmpty(annotation.consumes())));
      context.operationBuilder().produces(asSet(nullToEmpty(annotation.produces())));
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }


  private Set<String> asSet(String mediaTypes) {
    return newHashSet(Splitter.on(',')
            .trimResults()
            .omitEmptyStrings()
            .splitToList(mediaTypes));
  }


}
