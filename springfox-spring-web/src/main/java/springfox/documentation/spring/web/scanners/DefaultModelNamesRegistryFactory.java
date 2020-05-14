package springfox.documentation.spring.web.scanners;

import com.fasterxml.classmate.ResolvedType;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.ModelNamesRegistry;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ModelNamesRegistryFactoryPlugin;
import springfox.documentation.spi.service.contexts.ModelSpecificationRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultModelNamesRegistryFactory implements ModelNamesRegistryFactoryPlugin {
  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public ModelNamesRegistry modelNamesRegistry(ModelSpecificationRegistry registry) {
    return new DefaultModelNamesRegistry(registry);
  }

  private static class DefaultModelNamesRegistry implements ModelNamesRegistry {
    private final ModelSpecificationRegistry modelRegistry;
    private final Map<ModelKey, String> modelStems = new HashMap<>();
    private final Map<Set<ResolvedType>, String> validationSuffixes = new HashMap<>();
    private final Map<ModelKey, String> requestResponseSuffixes = new HashMap<>();
    private final Map<ModelKey, String> modelKeyToName;

    DefaultModelNamesRegistry(ModelSpecificationRegistry modelRegistry) {
      this.modelRegistry = modelRegistry;
      modelRegistry.modelKeys()
                   .forEach(this::processKeys);
      modelKeyToName = modelRegistry.modelKeys()
                                    .stream()
                                    .collect(Collectors.toMap(
                                        Function.identity(),
                                        k -> String.format(
                                            "%s%s%s",
                                            modelStems.get(k),
                                            validationSuffixes.getOrDefault(
                                                k.getValidationGroupDiscriminators(),
                                                ""),
                                            requestResponseSuffixes.getOrDefault(
                                                k,
                                                ""))));
    }

    private void processKeys(ModelKey modelKey) {
      boolean hasRequestResponsePair = modelRegistry.hasRequestResponsePairs(modelKey);
      Collection<ModelKey> validationModels = modelRegistry.modelsDifferingOnlyInValidationGroups(modelKey);
      Collection<ModelKey> sameNameDifferentNamespace = modelRegistry.modelsWithSameNameAndDifferentNamespace(modelKey);
      int index = 0;
      for (ModelKey key : sameNameDifferentNamespace) {
        int modelIndex = index;
        modelStems.computeIfAbsent(
            key,
            k -> String.format(
                "%s%s",
                k.getQualifiedModelName().getName(),
                modelIndex));
        index++;
      }
      modelStems.computeIfAbsent(
          modelKey,
          k -> k.getQualifiedModelName().getName());
      index = 0;
      for (ModelKey key : validationModels) {
        int modelIndex = index;
        validationSuffixes.computeIfAbsent(
            key.getValidationGroupDiscriminators(),
            k -> "_" + modelIndex);
        index++;
      }
      if (hasRequestResponsePair) {
        requestResponseSuffixes.computeIfAbsent(
            modelKey,
            k -> k.isResponse() ? "Res" : "Req");
      } else if (modelKey.getViewDiscriminator().isPresent()) {
        requestResponseSuffixes.putIfAbsent(
            modelKey,
            "View");
      }
    }

    @Override
    public Map<String, ModelSpecification> modelsByName() {
      return modelKeyToName.entrySet()
                           .stream()
                           .collect(Collectors.toMap(
                               Map.Entry::getValue,
                               e -> modelRegistry.modelSpecificationFor(e.getKey())));
    }

    @Override
    public Optional<String> nameByKey(ModelKey key) {
      return Optional.ofNullable(modelKeyToName.get(modelRegistry.effectiveModelKeyFor(key)));
    }
  }
}
