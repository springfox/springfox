package com.mangofactory.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.documentation.schema.TypeNameExtractor;
import com.mangofactory.documentation.service.model.ResolvedMethodParameter;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*;

@Component
public class ParameterDataTypeReader implements ParameterBuilderPlugin {
  private final TypeNameExtractor nameExtractor;

  @Autowired
  public ParameterDataTypeReader(TypeNameExtractor nameExtractor) {
    this.nameExtractor = nameExtractor;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public void apply(ParameterContext context) {
    ResolvedMethodParameter methodParameter = context.resolvedMethodParameter();
    ResolvedType parameterType = methodParameter.getResolvedParameterType();
    parameterType = context.alternateFor(parameterType);
    //Multi-part file trumps any other annotations
    if (MultipartFile.class.isAssignableFrom(parameterType.getErasedType())) {
      context.parameterBuilder().dataType("File");
    } else {
      String typeName = nameExtractor.typeName(inputParam(parameterType, context.getDocumentationType(),
              context.getAlternateTypeProvider()));
      context.parameterBuilder().dataType(typeName);
    }
  }
}
