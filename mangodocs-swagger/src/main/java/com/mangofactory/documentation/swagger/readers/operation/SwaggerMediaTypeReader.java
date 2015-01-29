package com.mangofactory.documentation.swagger.readers.operation;

import com.google.common.base.Splitter;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Strings.*;
import static com.mangofactory.documentation.swagger.common.SwaggerPluginSupport.*;
import static org.springframework.core.annotation.AnnotationUtils.*;

@Component
public class SwaggerMediaTypeReader implements OperationBuilderPlugin {
  @Override
  public void apply(OperationContext context) {
    ApiOperation annotation = findAnnotation(context.getHandlerMethod().getMethod(), ApiOperation.class);
    if (null != annotation) {
      context.operationBuilder().consumes(asList(nullToEmpty(annotation.consumes())));
      context.operationBuilder().produces(asList(nullToEmpty(annotation.produces())));
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return pluginDoesApply(delimiter);
  }


  private List<String> asList(String mediaTypes) {
    return Splitter.on(',')
            .trimResults()
            .omitEmptyStrings()
            .splitToList(mediaTypes);
  }


}
