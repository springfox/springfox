package springdox.documentation.schema;


import springdox.documentation.spi.schema.contexts.ModelContext;

public interface ModelProvider {
  com.google.common.base.Optional<Model> modelFor(ModelContext modelContext);

  java.util.Map<String, Model> dependencies(ModelContext modelContext);
}
