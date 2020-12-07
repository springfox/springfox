package springfox.documentation.spi.service.contexts;

import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelSpecification;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ModelSpecificationRegistry {
  ModelSpecification modelSpecificationFor(ModelKey key);

  boolean hasRequestResponsePairs(ModelKey test);

  Map<ModelKey, String> getSuffixesForEqualsModels(ModelKey test);

  Collection<ModelKey> modelsWithSameNameAndDifferentNamespace(ModelKey test);

  Set<ModelKey> modelKeys();
}
