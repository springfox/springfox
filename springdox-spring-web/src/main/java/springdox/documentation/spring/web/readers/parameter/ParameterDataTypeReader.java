package springdox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import springdox.documentation.schema.Collections;
import springdox.documentation.schema.Maps;
import springdox.documentation.schema.ModelRef;
import springdox.documentation.schema.TypeNameExtractor;
import springdox.documentation.service.ResolvedMethodParameter;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.schema.contexts.ModelContext;
import springdox.documentation.spi.service.ParameterBuilderPlugin;
import springdox.documentation.spi.service.contexts.ParameterContext;

import java.io.File;

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
      ModelContext modelContext = ModelContext.inputParam(parameterType, context.getDocumentationType(),
              context.getAlternateTypeProvider());
      context.parameterBuilder()
              .type(parameterType)
              .modelRef(modelRef(parameterType, modelContext));
    }
    
  }
  private ModelRef modelRef(ResolvedType type, ModelContext modelContext) {
    if (Collections.isContainerType(type)) {
      ResolvedType collectionElementType = Collections.collectionElementType(type);
      String elementTypeName = nameExtractor.typeName(ModelContext.fromParent(modelContext, collectionElementType));
      return new ModelRef(Collections.containerType(type), elementTypeName);
    }
    if (Maps.isMapType(type)) {
      String elementTypeName = nameExtractor.typeName(ModelContext.fromParent(modelContext, Maps.mapValueType(type)));
      return new ModelRef("Map", elementTypeName, true);
    }
    String typeName = nameExtractor.typeName(ModelContext.fromParent(modelContext, type));
    return new ModelRef(typeName);
  }
}
