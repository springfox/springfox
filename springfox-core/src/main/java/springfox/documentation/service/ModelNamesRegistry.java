package springfox.documentation.service;

import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelSpecification;

import java.util.Map;
import java.util.Optional;

public interface ModelNamesRegistry {
  Map<String, ModelSpecification> modelsByName();

  Optional<String> nameByKey(ModelKey key);
}
