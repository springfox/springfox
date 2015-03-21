package springdox.documentation.spi.schema.contexts;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Objects;
import springdox.documentation.builders.ModelBuilder;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.schema.AlternateTypeProvider;
import springdox.documentation.spi.schema.GenericTypeNamingStrategy;

import java.lang.reflect.Type;
import java.util.Set;

import static com.google.common.collect.Sets.*;

public class ModelContext {
  private final Type type;
  private final boolean returnType;
  private final DocumentationType documentationType;

  private final ModelContext parentContext;
  private final Set<ResolvedType> seenTypes = newHashSet();
  private final ModelBuilder modelBuilder;
  private final AlternateTypeProvider alternateTypeProvider;
  private GenericTypeNamingStrategy genericNamingStrategy;

  ModelContext(Type type, boolean returnType, DocumentationType documentationType,
               AlternateTypeProvider alternateTypeProvider,
               GenericTypeNamingStrategy genericNamingStrategy) {
    this.documentationType = documentationType;
    this.alternateTypeProvider = alternateTypeProvider;
    this.genericNamingStrategy = genericNamingStrategy;
    this.parentContext = null;
    this.type = type;
    this.returnType = returnType;
    this.modelBuilder = new ModelBuilder();
  }

  ModelContext(ModelContext parentContext, ResolvedType input) {
    this.parentContext = parentContext;
    this.type = input;
    this.returnType = parentContext.isReturnType();
    this.documentationType = parentContext.getDocumentationType();
    this.modelBuilder = new ModelBuilder();
    this.alternateTypeProvider = parentContext.alternateTypeProvider;
  }

  public Type getType() {
    return type;
  }

  public ResolvedType resolvedType(TypeResolver resolver) {
    return resolver.resolve(getType());
  }

  public boolean isReturnType() {
    return returnType;
  }

  public AlternateTypeProvider getAlternateTypeProvider() {
    return alternateTypeProvider;
  }

  public ResolvedType alternateFor(ResolvedType resolved) {
    return alternateTypeProvider.alternateFor(resolved);
  }

  public static ModelContext inputParam(Type type,
      DocumentationType documentationType,
      AlternateTypeProvider alternateTypeRules,
      GenericTypeNamingStrategy genericNamingStrategy) {

    return new ModelContext(type, false, documentationType, alternateTypeRules, genericNamingStrategy);
  }

  public static ModelContext returnValue(Type type,
      DocumentationType documentationType,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericNamingStrategy) {

    return new ModelContext(type, true, documentationType, alternateTypeProvider, genericNamingStrategy);
  }

  public static ModelContext fromParent(ModelContext context, ResolvedType input) {
    return new ModelContext(context, input);
  }

  public boolean hasSeenBefore(ResolvedType resolvedType) {
    return seenTypes.contains(resolvedType)
            || seenTypes.contains(new TypeResolver().resolve(resolvedType.getErasedType()))
            || parentHasSeenBefore(resolvedType);
  }

  public DocumentationType getDocumentationType() {
    return documentationType;
  }

  private boolean parentHasSeenBefore(ResolvedType resolvedType) {
    if (parentContext == null) {
      return false;
    }
    return parentContext.hasSeenBefore(resolvedType);
  }

  public GenericTypeNamingStrategy getGenericNamingStrategy() {
    if (parentContext == null) {
      return genericNamingStrategy;
    }
    return parentContext.getGenericNamingStrategy();
  }

  public ModelBuilder getBuilder() {
    return modelBuilder;
  }

  public void seen(ResolvedType resolvedType) {
    seenTypes.add(resolvedType);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)  {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ModelContext that = (ModelContext) o;

    return Objects.equal(type, that.type) &&
            Objects.equal(documentationType, that.documentationType) &&
            Objects.equal(returnType, that.returnType);

  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type, documentationType, returnType);
  }
}
