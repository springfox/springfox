package springdox.documentation.spi.service.contexts;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.core.MethodParameter;
import springdox.documentation.builders.ParameterBuilder;
import springdox.documentation.service.ResolvedMethodParameter;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.schema.AlternateTypeProvider;

public class ParameterContext {
  private final ParameterBuilder parameterBuilder;
  private final ResolvedMethodParameter resolvedMethodParameter;
  private final DocumentationContext documentationContext;

  public ParameterContext(ResolvedMethodParameter resolvedMethodParameter,
                          ParameterBuilder parameterBuilder,
                          DocumentationContext documentationContext) {
    this.parameterBuilder = parameterBuilder;
    this.resolvedMethodParameter = resolvedMethodParameter;
    this.documentationContext = documentationContext;
  }

  public ResolvedMethodParameter resolvedMethodParameter() {
    return resolvedMethodParameter;
  }

  public MethodParameter methodParameter() {
    return resolvedMethodParameter.getMethodParameter();
  }

  public ParameterBuilder parameterBuilder() {
    return parameterBuilder;
  }

  public DocumentationContext getDocumentationContext() {
    return documentationContext;
  }

  public DocumentationType getDocumentationType() {
    return documentationContext.getDocumentationType();
  }

  public ResolvedType alternateFor(ResolvedType parameterType) {
    return getAlternateTypeProvider().alternateFor(parameterType);
  }

  public AlternateTypeProvider getAlternateTypeProvider() {
    return documentationContext.getAlternateTypeProvider();
  }
}
