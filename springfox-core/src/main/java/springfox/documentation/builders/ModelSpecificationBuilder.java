package springfox.documentation.builders;

import org.springframework.lang.NonNull;
import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static springfox.documentation.builders.NoopValidator.*;

public class ModelSpecificationBuilder {
  private ModelFacetsBuilder facetsBuilder;
  private String name;
  private ScalarModelSpecification scalar;
  private CollectionSpecificationBuilder collection;
  private MapSpecificationBuilder map;
  private ReferenceModelSpecificationBuilder referenceModel;
  private CompoundModelSpecificationBuilder compoundModelBuilder;
  private final Validator<ModelSpecificationBuilder> validator = this::validateSpecification;

  public ModelSpecificationBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ModelSpecificationBuilder facets(@NonNull Consumer<ModelFacetsBuilder> facets) {
    if (facetsBuilder == null) {
      facetsBuilder = new ModelFacetsBuilder();
    }
    facets.accept(facetsBuilder);
    return this;
  }

  public ModelSpecificationBuilder scalarModel(ScalarType type) {
    if (type != null) {
      this.scalar = new ScalarModelSpecification(type);
    }
    return this;
  }

  private CompoundModelSpecificationBuilder compoundModelBuilder() {
    if (compoundModelBuilder == null) {
      this.compoundModelBuilder = new CompoundModelSpecificationBuilder();
    }
    return compoundModelBuilder;
  }

  public ModelSpecificationBuilder compoundModel(@NonNull Consumer<CompoundModelSpecificationBuilder> compound) {
    compound.accept(compoundModelBuilder());
    return this;
  }

  public ModelSpecificationBuilder collectionModel(@NonNull Consumer<CollectionSpecificationBuilder> consumer) {
    consumer.accept(collectionBuilder());
    return this;
  }

  private CollectionSpecificationBuilder collectionBuilder() {
    if (collection == null) {
      collection = new CollectionSpecificationBuilder();
    }
    return collection;
  }

  public ModelSpecificationBuilder mapModel(@NonNull Consumer<MapSpecificationBuilder> consumer) {
    consumer.accept(mapBuilder());
    return this;
  }

  private MapSpecificationBuilder mapBuilder() {
    if (map == null) {
      map = new MapSpecificationBuilder();
    }
    return map;
  }

  public ModelSpecificationBuilder referenceModel(Consumer<ReferenceModelSpecificationBuilder> modelKey) {
    modelKey.accept(referenceModelBuilder());
    return this;
  }

  private ReferenceModelSpecificationBuilder referenceModelBuilder() {
    if (referenceModel == null) {
      referenceModel = new ReferenceModelSpecificationBuilder();
    }
    return referenceModel;
  }

  public ModelSpecification build() {
    CompoundModelSpecification compoundModel =
        compoundModelBuilder != null ? compoundModelBuilder.build() : null;
    List<ValidationResult> results = validator.validate(this);
    if (logProblems(results).size() > 0) {
      return null;
    }
    return new ModelSpecification(
        name,
        facetsBuilder != null ? facetsBuilder.build() : null,
        scalar,
        compoundModel,
        collection != null ? collection.build() : null,
        map != null ? map.build() : null,
        referenceModel != null ? referenceModel.build() : null);
  }

  public ModelSpecificationBuilder copyOf(ModelSpecification other) {
    if (other != null) {
      ScalarType scalar = other.getScalar()
          .map(ScalarModelSpecification::getType)
          .orElse(null);
      ReferenceModelSpecification reference = other.getReference().orElse(null);
      CompoundModelSpecification compound = other.getCompound().orElse(null);
      CollectionSpecification collection = other.getCollection().orElse(null);
      MapSpecification map = other.getMap().orElse(null);

      this.scalar = null;
      this.map = null;
      this.collection = null;
      this.referenceModel = null;
      this.compoundModelBuilder = new CompoundModelSpecificationBuilder();

      this.name(other.getName())
          .scalarModel(scalar)
          .referenceModel(r -> r.copyOf(reference))
          .compoundModel(cm -> cm.copyOf(compound))
          .collectionModel(c -> c.copyOf(collection))
          .mapModel(m -> m.copyOf(map))
          .facets(f -> f.copyOf(other.getFacets().orElse(null)));
    }
    return this;
  }

  private List<ValidationResult> validateSpecification(ModelSpecificationBuilder builder) {
    List<ValidationResult> validationResults = new ArrayList<>();
    long specCount = Stream.of(scalar,
        safeCompoundModelBuild(),
        safeCollectionBuild(),
        safeMapBuild(),
        safeReferenceBuild())
        .filter(Objects::nonNull)
        .count();

    if (specCount == 0) {
      validationResults.add(new ValidationResult(
          "ModelSpecification",
          "spec",
          "At least one type of specification is required"));
    }
    if (specCount > 1) {
      validationResults.add(new ValidationResult(
          "ModelSpecification",
          "spec",
          "Only one of the specifications should be non null"));
    }
    return validationResults;
  }

  private Object safeCompoundModelBuild() {
    return compoundModelBuilder != null ? compoundModelBuilder.build() : null;
  }

  private Object safeCollectionBuild() {
    return collection != null ? collection.build() : null;
  }

  private Object safeMapBuild() {
    return map != null ? map.build() : null;
  }

  private Object safeReferenceBuild() {
    return referenceModel != null ? referenceModel.build() : null;
  }

  public ModelSpecificationBuilder maybeConvertToScalar(ScalarType scalar) {
    scalarModel(scalar);
    if (compoundModelBuilder != null) {
      compoundModelBuilder = null;
    }
    if (collection != null) {
      collection = null;
    }
    return this;
  }
}