package springfox.documentation.spring.web.scanners;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.spi.service.contexts.ModelSpecificationRegistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelSpecificationRegistryBuilder {
  private final MultiValueMap<ModelKey, ModelSpecification> modelsLookupByKey = new LinkedMultiValueMap<>();
  private final Map<ModelKey, ModelKey> referenceKeyToEffectiveKey = new HashMap<>();

  public void add(ModelSpecification modelSpecification) {
    modelSpecification.key()
                      .ifPresent(key -> {
                        if (modelSpecification.effectiveKey().isPresent()
                            && !Objects.equals(
                            key,
                            modelSpecification.effectiveKey().get())) {
                          referenceKeyToEffectiveKey.put(
                              key,
                              modelSpecification.effectiveKey().get());
                          modelsLookupByKey.add(
                              modelSpecification.effectiveKey().get(),
                              modelSpecification); //To lookup model by effective key
                        }
                        modelsLookupByKey.add(
                            key,
                            modelSpecification); //To lookup model by actual key
                      });
  }

  public void addAll(Collection<ModelSpecification> modelSpecifications) {
    modelSpecifications.forEach(this::add);
  }

  public ModelSpecificationRegistry build() {
    return new DefaultModelSpecificationRegistry(
        modelsLookupByKey,
        referenceKeyToEffectiveKey);
  }

  private static class DefaultModelSpecificationRegistry implements ModelSpecificationRegistry {
    private final Map<ModelKey, ModelSpecification> modelsLookupByKey;
    private final Map<ModelKey, ModelKey> referenceKeyToEffectiveKey;

    DefaultModelSpecificationRegistry(
        MultiValueMap<ModelKey, ModelSpecification> modelsLookupByKey,
        Map<ModelKey, ModelKey> referenceKeyToEffectiveKey) {

      this.referenceKeyToEffectiveKey = referenceKeyToEffectiveKey;
      this.modelsLookupByKey = mergeValuesAndCovertToMap(modelsLookupByKey);
    }

    private Map<ModelKey, ModelSpecification> mergeValuesAndCovertToMap(
        MultiValueMap<ModelKey, ModelSpecification> modelsLookupByKey) {

      Map<ModelKey, ModelSpecification> merged
          = modelsLookupByKey.entrySet().stream()
                             .filter(entry -> entry.getValue().size() >= 1)
                             .collect(Collectors.toMap(
                                 Map.Entry::getKey,
                                 e -> mergeModels(e.getValue())));
      referenceKeyToEffectiveKey.keySet()
                                .forEach(k -> {
                                  ModelSpecification mergedModel = mergeModels(
                                      Arrays.asList(
                                          merged.get(k),
                                          merged.get(effectiveModelKeyFor(k))));
                                  merged.put(
                                      k,
                                      mergedModel);
                                  merged.put(
                                      effectiveModelKeyFor(k),
                                      mergedModel);
                                });
      return merged;
    }

    private ModelSpecification mergeModels(List<ModelSpecification> models) {
      ModelSpecificationBuilder modelBuilder = new ModelSpecificationBuilder();
      if (models.size() > 1) {
        return models.stream()
                     .reduce((first, second) -> modelBuilder.copyOf(first).copyOf(second).build())
                     .orElseThrow(() -> new IllegalStateException("Could not combine the models"));
      }
      return models.stream().findFirst().orElseThrow(() -> new IllegalStateException("Expecting only non-empty sets"));
    }

    @Override
    public ModelKey effectiveModelKeyFor(ModelKey key) {
      return referenceKeyToEffectiveKey.getOrDefault(
          key,
          key);
    }

    @Override
    public ModelSpecification modelSpecificationFor(ModelKey key) {
      return modelsLookupByKey.get(key);
    }

    @Override
    public boolean hasRequestResponsePairs(ModelKey test) {
      return modelsLookupByKey.containsKey(test.flippedResponse())
          && Objects.equals(
          modelsLookupByKey.get(
              test),
          modelsLookupByKey.get(
              test.flippedResponse()));

    }

    @Override
    public Collection<ModelKey> modelsDifferingOnlyInValidationGroups(ModelKey test) {
      return modelsLookupByKey.keySet().stream()
                              .filter(mk -> mk.getQualifiedModelName()
                                              .equals(test.getQualifiedModelName())
                                  && Objects.equals(mk.getViewDiscriminator(), test.getViewDiscriminator())
                                  && mk.isResponse() == test.isResponse()
                                  && !modelsLookupByKey.get(mk).equals(modelsLookupByKey.get(test)))
                              .collect(Collectors.toSet());
    }

    @Override
    public Collection<ModelKey> modelsWithSameNameAndDifferentNamespace(ModelKey test) {
      return modelsLookupByKey.keySet().stream()
                              .filter(mk -> mk.getQualifiedModelName().getName()
                                              .equals(test.getQualifiedModelName().getName())
                                  && !mk.getQualifiedModelName().getNamespace()
                                        .equals(test.getQualifiedModelName().getNamespace()))
                              .collect(Collectors.toSet());
    }

    @Override
    public Set<ModelKey> modelKeys() {
      return modelsLookupByKey.keySet();
    }
  }
}
