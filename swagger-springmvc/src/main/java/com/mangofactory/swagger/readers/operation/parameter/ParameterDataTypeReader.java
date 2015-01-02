package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.mangofactory.schema.ResolvedTypes.*;

@Component
public class ParameterDataTypeReader implements Command<RequestMappingContext> {
  private final AlternateTypeProvider alternateTypeProvider;

  @Autowired
  public ParameterDataTypeReader(AlternateTypeProvider alternateTypeProvider) {
    this.alternateTypeProvider = alternateTypeProvider;
  }

  @Override
  public void execute(RequestMappingContext context) {
    ResolvedMethodParameter methodParameter = (ResolvedMethodParameter) context.get("resolvedMethodParameter");
    ResolvedType parameterType = methodParameter.getResolvedParameterType();
    parameterType = alternateTypeProvider.alternateFor(parameterType);
    //Multi-part file trumps any other annotations
    if (MultipartFile.class.isAssignableFrom(parameterType.getErasedType())) {
      context.put("dataType", "File");
    } else {
      context.put("dataType", parameterTypeName(parameterType));
    }
  }

}
