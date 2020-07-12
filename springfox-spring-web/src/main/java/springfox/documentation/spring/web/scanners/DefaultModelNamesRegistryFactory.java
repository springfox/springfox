package springfox.documentation.spring.web.scanners;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.QualifiedModelName;
import springfox.documentation.service.ModelNamesRegistry;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ModelNamesRegistryFactoryPlugin;
import springfox.documentation.spi.service.contexts.ModelSpecificationRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
      adjustForNameCollisions();
    }

    private void adjustForNameCollisions() {
      MultiValueMap<String, ModelKey> nameToKey = new LinkedMultiValueMap<>();
      modelKeyToName.forEach((k, v) -> nameToKey.add(v, k));
      nameToKey.entrySet()
          .stream()
          .filter(e -> e.getValue().size() > 1)
          .forEach(e -> {
            Map<QualifiedModelName, String> nameToSuffix = nameSuffixLookup(e);
            e.getValue()
                .forEach(modelKey ->
                             modelKeyToName.put(
                                 modelKey,
                                 String.format(
                                     "%s%s",
                                     modelKeyToName.get(modelKey),
                                     nameToSuffix.getOrDefault(modelKey.getQualifiedModelName(), ""))));
          });
    }

    private Map<QualifiedModelName, String> nameSuffixLookup(Map.Entry<String, List<ModelKey>> e) {
      Map<QualifiedModelName, String> modelSuffix = new HashMap<>();
      Set<QualifiedModelName> distinctModels = e.getValue()
          .stream()
          .map(ModelKey::getQualifiedModelName)
          .collect(Collectors.toSet());
      if (distinctModels.size() > 1) {
        int index = 0;
        for (QualifiedModelName key : distinctModels) {
          int modelIndex = index;
          modelSuffix.putIfAbsent(
              key,
              String.valueOf(modelIndex));
          index++;
        }
      }
      return modelSuffix;
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
            k -> modelIndex == 0 ? "" : "_" + modelIndex);
        index++;
      }
      if (hasRequestResponsePair) {
        requestResponseSuffixes.computeIfAbsent(
            modelKey,
            k -> k.isResponse() ? "Res" : "Req");
      } else if (modelKey.getViewDiscriminator().isPresent()) {
        requestResponseSuffixes.putIfAbsent(
            modelKey,
            modelKey.getViewDiscriminator().get().getErasedType().getSimpleName() + "View");
      }
    }

    @Override
    public Map<String, ModelSpecification> modelsByName() {
      Map<String, ModelSpecification> map = new HashMap<>();
      modelKeyToName.forEach((key, value) -> map.putIfAbsent(
          value,
          new ModelSpecificationBuilder()
              .copyOf(modelRegistry.modelSpecificationFor(key))
              .facets(f -> f.title(value))
              .build()));
      return map;
    }

    @Override
    public Optional<String> nameByKey(ModelKey key) {
      return Optional.ofNullable(modelKeyToName.get(key));
    }
  }
}
