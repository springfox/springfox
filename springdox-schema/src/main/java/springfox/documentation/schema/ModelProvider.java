package springfox.documentation.schema;


import springfox.documentation.spi.schema.contexts.ModelContext;

public interface ModelProvider {
  com.google.common.base.Optional<Model> modelFor(ModelContext modelContext);

  java.util.Map<String, Model> dependencies(ModelContext modelContext);
}
