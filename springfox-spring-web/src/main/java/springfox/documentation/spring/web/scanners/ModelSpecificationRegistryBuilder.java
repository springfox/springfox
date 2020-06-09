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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
        modelByQName.addIfAbsent(key.getQualifiedModelName(), modelSpecification);
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
    Set<ModelKey> seen = new HashSet<>();

    for (Map.Entry<QualifiedModelName, List<ModelSpecification>> eachEntry : modelByQName.entrySet()) {
      List<ModelSpecification> models = eachEntry.getValue();
      if (models.size() > 1) {
        for (int i = 0; i < (models.size() - 1); i++) {
          for (int j = 1; j < models.size(); j++) {
            models.get(i).getCompound()
                .map(CompoundModelSpecification::getModelKey)
                .ifPresent(seen::add);
            models.get(j).getCompound()
                .map(CompoundModelSpecification::getModelKey)
                .ifPresent(seen::add);
            if (sameModel(models.get(i), models.get(j), referenceKeyToEffectiveKey, seen)) {
              LOGGER.trace(
                  "Models were equivalent {} and {}",
                  models.get(i).key().orElse(null),
                  models.get(j).key().orElse(null));
              referenceKeyToEffectiveKey.add(models.get(i).key().get(), models.get(j).key().get());
              referenceKeyToEffectiveKey.add(models.get(j).key().get(), models.get(i).key().get());
            } else {
              LOGGER.trace(
                  "Models were different {} and {}",
                  models.get(i).key().orElse(null),
                  models.get(j).key().orElse(null));
            }
          }
        }
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
      Set<ModelKey> seen) {
    if (Objects.equals(first, second)) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    LOGGER.trace(
        "Comparing models {} and {}",
        first.key()
            .map(ModelKey::toString)
            .orElse("scalar property"),
        second.key()
            .map(ModelKey::toString)
            .orElse("scalar property"));
    return Objects.equals(first.getScalar(), second.getScalar()) &&
        sameCompoundModel(
            first.getCompound().orElse(null),
            second.getCompound().orElse(null),
            referenceKeyToEffectiveKey,
            seen)
        && sameCollectionModel(
        first,
        second,
        referenceKeyToEffectiveKey,
        seen)
        && sameMapModel(first, second, referenceKeyToEffectiveKey, seen)
        && Objects.equals(first.getFacets(), second.getFacets())
        && Objects.equals(first.getName(), second.getName())
        && equivalentReference(
        first.getReference().map(ReferenceModelSpecification::getKey).orElse(null),
        second.getReference().map(ReferenceModelSpecification::getKey).orElse(null),
        referenceKeyToEffectiveKey,
        seen);
  }

  private boolean sameCollectionModel(
      ModelSpecification first,
      ModelSpecification second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      Set<ModelKey> seen) {
    ModelSpecification firstCollection = first.getCollection()
        .map(CollectionSpecification::getModel)
        .orElse(null);
    ModelSpecification secondCollection = second.getCollection()
        .map(CollectionSpecification::getModel)
        .orElse(null);
    if (firstCollection == secondCollection) {
      return true;
    }
    if (firstCollection == null || secondCollection == null) {
      return false;
    }
    LOGGER.trace(
        "Comparing collections {} and {}",
        firstCollection.key().orElse(null),
        secondCollection.key().orElse(null));

    return sameModel(firstCollection, secondCollection, referenceKeyToEffectiveKey, seen);
  }

  private boolean sameMapModel(
      ModelSpecification first,
      ModelSpecification second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      Set<ModelKey> seen) {
    if (first == second) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    LOGGER.trace(
        "Comparing Map with values {} and {}",
        first.getMap()
            .map(MapSpecification::getValue)
            .flatMap(ModelSpecification::key)
            .orElse(null),
        second.getMap()
            .map(MapSpecification::getValue)
            .flatMap(ModelSpecification::key)
            .orElse(null));
    return sameModel(
        first.getMap().map(MapSpecification::getKey).orElse(null),
        second.getMap().map(MapSpecification::getKey).orElse(null),
        referenceKeyToEffectiveKey,
        seen)
        && sameModel(
        first.getMap().map(MapSpecification::getKey).orElse(null),
        second.getMap().map(MapSpecification::getKey).orElse(null),
        referenceKeyToEffectiveKey,
        seen);
  }

  private boolean sameCompoundModel(
      CompoundModelSpecification first,
      CompoundModelSpecification second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      Set<ModelKey> seen) {
    if (first == second) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    LOGGER.trace("Comparing compound specs {} and {}", first, second);
    return sameProperties(first.getProperties(), second.getProperties(), referenceKeyToEffectiveKey, seen) &&
        Objects.equals(first.getMaxProperties(), second.getMaxProperties()) &&
        Objects.equals(first.getMinProperties(), second.getMinProperties()) &&
        Objects.equals(first.getDiscriminator(), second.getDiscriminator());
  }

  private boolean sameProperties(
      Collection<PropertySpecification> first,
      Collection<PropertySpecification> second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      Set<ModelKey> seen) {
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
      if (seenProperty(firstProperty, seen) && seenProperty(secondProperty, seen)) {
        continue;
      }
      if (!sameProperty(firstProperty, secondMap.get(name), referenceKeyToEffectiveKey, seen)) {
        LOGGER.trace("Properties did not match {} and {}", firstProperty.getName(), secondProperty.getName());
        return false;
      } else {
        LOGGER.trace("Properties matched {} and {}", firstProperty.getName(), secondProperty.getName());
      }
    }
    return true;
  }

  private boolean seenProperty(
      PropertySpecification firstProperty,
      Set<ModelKey> seen) {
    return firstProperty.getType().getReference().isPresent() &&
        firstProperty.getType().getReference()
            .map(ReferenceModelSpecification::getKey)
            .map(seen::contains)
            .orElse(false);
  }

  @SuppressWarnings("CyclomaticComplexity")
  private boolean sameProperty(
      PropertySpecification first,
      PropertySpecification second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      Set<ModelKey> seen) {
    first.getType().getCompound()
        .map(CompoundModelSpecification::getModelKey)
        .ifPresent(seen::add);
    second.getType().getCompound()
        .map(CompoundModelSpecification::getModelKey)
        .ifPresent(seen::add);
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

  @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity"})
  private boolean equivalentReference(
      ModelKey first,
      ModelKey second,
      MultiValueMap<ModelKey, ModelKey> referenceKeyToEffectiveKey,
      Set<ModelKey> seen) {
    if (first == second) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    if (seen.contains(first)
        || seen.contains(second)) {
      return true;
    }
    LOGGER.trace("Comparing references {} and {}", first, second);
    if ((referenceKeyToEffectiveKey.containsKey(first)
             && referenceKeyToEffectiveKey.get(first).contains(second) ||
             (referenceKeyToEffectiveKey.containsKey(second)
                  && referenceKeyToEffectiveKey.get(second).contains(first)))) {
      return true;
    }
    seen.add(first);
    seen.add(second);
    boolean isSame = sameModel(modelByKey.get(first), modelByKey.get(second), referenceKeyToEffectiveKey, seen);
    if (isSame) {
      referenceKeyToEffectiveKey.addIfAbsent(first, second);
      referenceKeyToEffectiveKey.addIfAbsent(second, first);
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
