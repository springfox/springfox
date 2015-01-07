package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.springmvc.plugins.ParameterBuilderPlugin;
import com.mangofactory.springmvc.plugins.ParameterContext;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.mangofactory.schema.ResolvedTypes.*;

@Component
public class ParameterDataTypeReader implements ParameterBuilderPlugin {
  private final AlternateTypeProvider alternateTypeProvider;

  @Autowired
  public ParameterDataTypeReader(AlternateTypeProvider alternateTypeProvider) {
    this.alternateTypeProvider = alternateTypeProvider;
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
      context.parameterBuilder().dataType(parameterTypeName(parameterType));
    }
  }
}
