package com.mangofactory.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.schema.ModelRef;
import com.mangofactory.documentation.schema.TypeNameExtractor;
import com.mangofactory.documentation.service.ResolvedMethodParameter;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static com.mangofactory.documentation.schema.Collections.*;
import static com.mangofactory.documentation.schema.Maps.*;
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*;

@Component
public class ParameterDataTypeReader implements ParameterBuilderPlugin {
  private final TypeNameExtractor nameExtractor;
  private final TypeResolver resolver;

  @Autowired
  public ParameterDataTypeReader(TypeNameExtractor nameExtractor, TypeResolver resolver) {
    this.nameExtractor = nameExtractor;
    this.resolver = resolver;
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
      context.parameterBuilder()
              .type(resolver.resolve(File.class))
              .modelRef(new ModelRef("File"));
    } else {
      ModelContext modelContext = inputParam(parameterType, context.getDocumentationType(),
              context.getAlternateTypeProvider());
      context.parameterBuilder()
              .type(parameterType)
              .modelRef(modelRef(parameterType, modelContext));
    }
    
  }
  private ModelRef modelRef(ResolvedType type, ModelContext modelContext) {
    if (isContainerType(type)) {
      ResolvedType collectionElementType = collectionElementType(type);
      String elementTypeName = nameExtractor.typeName(fromParent(modelContext, collectionElementType));
      return new ModelRef(containerType(type), elementTypeName);
    }
    if (isMapType(type)) {
      String elementTypeName = nameExtractor.typeName(fromParent(modelContext, mapValueType(type)));
      return new ModelRef("Map", elementTypeName, true);
    }
    String typeName = nameExtractor.typeName(fromParent(modelContext, type));
    return new ModelRef(typeName);
  }
}
