package springdox.documentation.spi.service.contexts;

import com.google.common.collect.ImmutableSet;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.schema.AlternateTypeProvider;
import springdox.documentation.spi.schema.GenericTypeNamingStrategy;
import springdox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Type;
import java.util.Set;

import static com.google.common.collect.Sets.*;

public class OperationModelContextsBuilder {
  private final DocumentationType documentationType;
  private final AlternateTypeProvider alternateTypeProvider;
  private final GenericTypeNamingStrategy genericsNamingStrategy;
  private final Set<ModelContext> contexts = newHashSet();

  public OperationModelContextsBuilder(DocumentationType documentationType, AlternateTypeProvider alternateTypeProvider,
                                       GenericTypeNamingStrategy genericsNamingStrategy) {
    this.documentationType = documentationType;
    this.alternateTypeProvider = alternateTypeProvider;
    this.genericsNamingStrategy = genericsNamingStrategy;
  }

  public OperationModelContextsBuilder addReturn(Type type) {
    ModelContext returnValue
        = ModelContext.returnValue(type, documentationType, alternateTypeProvider, genericsNamingStrategy);
    this.contexts.add(returnValue);
    return this;
  }

  public OperationModelContextsBuilder addInputParam(Type type) {
    ModelContext inputParam
        = ModelContext.inputParam(type, documentationType, alternateTypeProvider, genericsNamingStrategy);
    this.contexts.add(inputParam);
    return this;
  }

  public Set<ModelContext> build() {
    return ImmutableSet.copyOf(contexts);
  }
}
