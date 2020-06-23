package springfox.documentation.spi.service.contexts;

import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelSpecification;

import java.util.Collection;
import java.util.Set;

public interface ModelSpecificationRegistry {
  ModelSpecification modelSpecificationFor(ModelKey key);

  boolean hasRequestResponsePairs(ModelKey test);

  Collection<ModelKey> modelsDifferingOnlyInValidationGroups(ModelKey test);

  Collection<ModelKey> modelsWithSameNameAndDifferentNamespace(ModelKey test);

  Set<ModelKey> modelKeys();
}
