package springfox.documentation.spring.web.scanners;

import org.slf4j.Logger;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.QualifiedModelName;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.spi.service.contexts.ModelSpecificationRegistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.*;

public class ModelSpecificationRegistryBuilder {
  private static final Logger LOGGER = getLogger(ModelSpecificationRegistryBuilder.class);
  private final MultiValueMap<QualifiedModelName, ModelSpecification> modelByQName = new LinkedMultiValueMap<>();
  private final Map<ModelKey, ModelSpecification> modelByKey = new HashMap<>();

  public void add(ModelSpecification modelSpecification) {
    modelSpecification.key().ifPresent(key -> {
      if (!modelByKey.containsKey(key)) {
        modelByKey.put(key, modelSpecification);
        if (!modelByQName.containsKey(key.getQualifiedModelName())) {
          modelByQName.add(key.getQualifiedModelName(), modelSpecification);
        }
        List<ModelSpecification> specsWithSameName = modelByQName.get(key.getQualifiedModelName());
        if (!specsWithSameName.contains(modelSpecification)) {
          modelByQName.add(key.getQualifiedModelName(), modelSpecification);
        }
      }
    });
  }

  public void addAll(Collection<ModelSpecification> modelSpecifications) {
    modelSpecifications.forEach(this::add);
  }

  public ModelSpecificationRegistry build() {
    MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey = new LinkedMultiValueMap<>();
    HashMap<List<ModelKey>, Boolean> seen = new HashMap<>();

    for (Map.Entry<QualifiedModelName, List<ModelSpecification>> eachEntry : modelByQName.entrySet()) {
      List<ModelSpecification> models = eachEntry.getValue();
      if (models.size() > 1) {
        LOGGER.trace(
            "Starting comparison of model with name {}. Models to compare {}", eachEntry.getKey(), models.size());
        for (int i = 0; i < (models.size() - 1); i++) {
          for (int j = 1; j < models.size(); j++) {
            if (models.get(i).key().isPresent() && models.get(j).key().isPresent()) {
              ModelKey first = models.get(j).key().get();
              ModelKey second = models.get(i).key().get();
              seen.putIfAbsent(Arrays.asList(second, first), true);
              if (sameModel(models.get(i), models.get(j), referenceKeyToEffectiveKey, seen)) {
                LOGGER.trace("Models were equivalent {} and {}", second, first);
                referenceKeyToEffectiveKey.add(second, first);
                referenceKeyToEffectiveKey.add(first, second);
              } else {
                LOGGER.trace("Models were different {} and {}", second, first);
              }
            } else {
              LOGGER.trace(
                  "Models were different {} and {}",
                  models.get(i).key().orElse(null),
                  models.get(j).key().orElse(null));
            }
          }
        }
        LOGGER.trace("Done comparison of models with name {}", eachEntry.getKey());
      }
    }
    return new DefaultModelSpecificationRegistry(
        modelByKey,
        referenceKeyToEffectiveKey);
  }

  private boolean sameModel(
      ModelSpecification first,
      ModelSpecification second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      HashMap<List<ModelKey>, Boolean> seen) {
    if (Objects.equals(first, second)) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    return Objects.equals(first.getScalar(), second.getScalar()) &&
        sameCompoundModel(
            first.getCompound().orElse(null),
            second.getCompound().orElse(null),
            referenceKeyToEffectiveKey,
            seen)
        && sameCollectionModel(
        first.getCollection().orElse(null),
        second.getCollection().orElse(null),
        referenceKeyToEffectiveKey, seen)
        && sameMapModel(
        first.getMap().orElse(null),
        second.getMap().orElse(null),
        referenceKeyToEffectiveKey, seen)
        && Objects.equals(first.getFacets(), second.getFacets())
        && Objects.equals(first.getName(), second.getName())
        && equivalentReference(
        first.getReference().map(ReferenceModelSpecification::getKey).orElse(null),
        second.getReference().map(ReferenceModelSpecification::getKey).orElse(null),
        referenceKeyToEffectiveKey,
        seen);
  }

  private boolean sameCollectionModel(
      CollectionSpecification first,
      CollectionSpecification second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      HashMap<List<ModelKey>, Boolean> seen) {
    if (first == second) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    LOGGER.trace(
        "Comparing collections {} and {}",
        first,
        first);

    return sameModel(first.getModel(), second.getModel(), referenceKeyToEffectiveKey, seen);
  }

  private boolean sameMapModel(
      MapSpecification first,
      MapSpecification second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      HashMap<List<ModelKey>, Boolean> seen) {
    if (first == second) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    LOGGER.trace("Comparing Map  {} and {}", first, second);
    return sameModel(first.getKey(), second.getKey(), referenceKeyToEffectiveKey, seen)
        && sameModel(first.getValue(), second.getValue(), referenceKeyToEffectiveKey, seen);
  }

  private boolean sameCompoundModel(
      CompoundModelSpecification first,
      CompoundModelSpecification second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      HashMap<List<ModelKey>, Boolean> seen) {
    if (first == second) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    LOGGER.trace("Comparing compound specs {} and {}", first, second);
    seen.put(Arrays.asList(first.getModelKey(), second.getModelKey()), true);
    boolean sameProperties = sameProperties(
        first.getProperties(),
        second.getProperties(),
        referenceKeyToEffectiveKey,
        seen);
    seen.put(Arrays.asList(first.getModelKey(), second.getModelKey()), sameProperties);
    return sameProperties &&
        Objects.equals(first.getMaxProperties(), second.getMaxProperties()) &&
        Objects.equals(first.getMinProperties(), second.getMinProperties()) &&
        Objects.equals(first.getDiscriminator(), second.getDiscriminator());
  }

  private boolean sameProperties(
      Collection<PropertySpecification> first,
      Collection<PropertySpecification> second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      HashMap<List<ModelKey>, Boolean> seen) {
    if (first.size() != second.size()) {
      return false;
    }
    Map<String, PropertySpecification> firstMap = first.stream()
                                                       .collect(Collectors.toMap(
                                                           PropertySpecification::getName,
                                                           Function.identity()));
    Map<String, PropertySpecification> secondMap = second.stream()
                                                         .collect(Collectors.toMap(
                                                             PropertySpecification::getName,
                                                             Function.identity()));
    if (!firstMap.keySet().equals(secondMap.keySet())) {
      return false;
    }
    for (String name : firstMap.keySet()) {
      PropertySpecification firstProperty = firstMap.get(name);
      PropertySpecification secondProperty = secondMap.get(name);
      if (!sameProperty(firstProperty, secondProperty, referenceKeyToEffectiveKey, seen)) {
        LOGGER.trace("Properties {} did not match", firstProperty.getName());
        return false;
      } else {
        LOGGER.trace("Properties  {} matched", firstProperty.getName());
      }
    }
    return true;
  }

  @SuppressWarnings("CyclomaticComplexity")
  private boolean sameProperty(
      PropertySpecification first,
      PropertySpecification second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      HashMap<List<ModelKey>, Boolean> seen) {
    LOGGER.trace("Comparing property {}", first.getName());
    return first.getPosition() == second.getPosition() &&
        Objects.equals(first.getName(), second.getName()) &&
        Objects.equals(first.getDescription(), second.getDescription()) &&
        sameModel(first.getType(), second.getType(), referenceKeyToEffectiveKey, seen) &&
        Objects.equals(first.getFacets(), second.getFacets()) &&
        Objects.equals(first.getNullable(), second.getNullable()) &&
        Objects.equals(first.getRequired(), second.getRequired()) &&
        Objects.equals(first.getReadOnly(), second.getReadOnly()) &&
        Objects.equals(first.getWriteOnly(), second.getWriteOnly()) &&
        Objects.equals(first.getDeprecated(), second.getDeprecated()) &&
        Objects.equals(first.getAllowEmptyValue(), second.getAllowEmptyValue()) &&
        Objects.equals(first.getHidden(), second.getHidden()) &&
        Objects.equals(first.getExample(), second.getExample()) &&
        Objects.equals(first.getDefaultValue(), second.getDefaultValue()) &&
        Objects.equals(first.getXml(), second.getXml()) &&
        Objects.equals(first.getVendorExtensions(), second.getVendorExtensions());
  }

  @SuppressWarnings({ "CyclomaticComplexity", "NPathComplexity" })
  private boolean equivalentReference(
      ModelKey first,
      ModelKey second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      HashMap<List<ModelKey>, Boolean> seen) {
    if (first == second) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    if (seen.containsKey(Arrays.asList(first, second))) {
      return seen.get(Arrays.asList(first, second));
    }
    LOGGER.trace("Comparing references {} and {}", first, second);
    seen.put(Arrays.asList(first, second), true);
    boolean isSame = sameModel(modelByKey.get(first), modelByKey.get(second), referenceKeyToEffectiveKey, seen);
    seen.put(Arrays.asList(first, second), isSame);
    if (isSame) {
      if (!referenceKeyToEffectiveKey.containsKey(first)) {
        referenceKeyToEffectiveKey.add(first, second);
      }
      if (!referenceKeyToEffectiveKey.containsKey(second)) {
        referenceKeyToEffectiveKey.add(second, first);
      }
    }
    return isSame;
  }

  private static class DefaultModelSpecificationRegistry implements ModelSpecificationRegistry {
    private final Map<ModelKey, ModelSpecification> modelsLookupByKey;
    private final MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKeys;

    DefaultModelSpecificationRegistry(
        Map<ModelKey, ModelSpecification> modelsLookupByKey,
        MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKeys) {

      this.referenceKeyToEffectiveKeys = referenceKeyToEffectiveKeys;
      this.modelsLookupByKey = modelsLookupByKey;
    }

    @Override
    public ModelSpecification modelSpecificationFor(ModelKey key) {
      return modelsLookupByKey.get(key);
    }

    @Override
    public boolean hasRequestResponsePairs(ModelKey test) {
      ModelKey other = test.flippedResponse();
      return modelsLookupByKey.containsKey(other)
          && !areEquivalent(test, other)
          && !areEquivalent(other, test);
    }

    private boolean areEquivalent(
        ModelKey first,
        ModelKey second) {
      return referenceKeyToEffectiveKeys.containsKey(first)
          && referenceKeyToEffectiveKeys.get(first).contains(second);
    }

    @Override
    public Collection<ModelKey> modelsDifferingOnlyInValidationGroups(ModelKey test) {
      return modelsLookupByKey.keySet().stream()
                              .filter(mk -> mk.getQualifiedModelName().equals(test.getQualifiedModelName())
                                  && Objects.equals(mk.getViewDiscriminator(), test.getViewDiscriminator())
                                  && mk.isResponse() == test.isResponse()
                                  && !areEquivalent(mk, test))
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
