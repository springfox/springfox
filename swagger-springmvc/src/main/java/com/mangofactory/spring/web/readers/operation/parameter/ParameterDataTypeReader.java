package com.mangofactory.spring.web.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.schema.TypeNameExtractor;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.spring.web.plugins.ParameterBuilderPlugin;
import com.mangofactory.spring.web.plugins.ParameterContext;
import com.mangofactory.spring.web.readers.operation.ResolvedMethodParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.mangofactory.schema.plugins.ModelContext.*;

@Component
public class ParameterDataTypeReader implements ParameterBuilderPlugin {
  private final AlternateTypeProvider alternateTypeProvider;
  private final TypeNameExtractor nameExtractor;

  @Autowired
  public ParameterDataTypeReader(AlternateTypeProvider alternateTypeProvider, TypeNameExtractor nameExtractor) {
    this.alternateTypeProvider = alternateTypeProvider;
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
    parameterType = alternateTypeProvider.alternateFor(parameterType);
    //Multi-part file trumps any other annotations
    if (MultipartFile.class.isAssignableFrom(parameterType.getErasedType())) {
      context.parameterBuilder().dataType("File");
    } else {
      String typeName = nameExtractor.typeName(inputParam(parameterType, context.getDocumentationType()));
      context.parameterBuilder().dataType(typeName);
    }
  }
}
