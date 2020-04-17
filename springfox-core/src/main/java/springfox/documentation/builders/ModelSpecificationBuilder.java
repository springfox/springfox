package springfox.documentation.builders;

import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;

import java.util.Arrays;
import java.util.Objects;

public class ModelSpecificationBuilder {
  private final String sourceIdentifier;
  private final ModelFacetsBuilder facetsBuilder = new ModelFacetsBuilder(this);
  private String name;
  private ScalarModelSpecification scalar;
  private CompoundModelSpecification compound;
  private CollectionSpecification collection;
  private MapSpecification map;
  private ReferenceModelSpecification reference;

  public ModelSpecificationBuilder(String sourceIdentifier) {
    this.sourceIdentifier = sourceIdentifier;
  }

  public ModelSpecificationBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ModelFacetsBuilder facetsBuilder() {
    return facetsBuilder;
  }

  public ModelSpecificationBuilder scalarModel(ScalarType type) {
    if (type != null) {
      this.scalar = new ScalarModelSpecification(type);
    }
    return this;
  }

  public ModelSpecificationBuilder scalarModel(ScalarModelSpecification scalarModelSpecification) {
    if (scalarModelSpecification != null) {
      this.scalar = scalarModelSpecification;
    }
    return this;
  }

  public ModelSpecificationBuilder compoundModel(CompoundModelSpecification compound) {
    this.compound = compound;
    return this;
  }

  public ModelSpecificationBuilder collectionModel(CollectionSpecification collection) {
    this.collection = collection;
    return this;
  }

  public ModelSpecificationBuilder mapModel(MapSpecification map) {
    this.map = map;
    return this;
  }

  public ModelSpecificationBuilder referenceModel(ReferenceModelSpecification reference) {
    this.reference = reference;
    return this;
  }

  public ModelSpecification build() {
    ensureValidSpecification(
        scalar,
        compound,
        reference,
        collection,
        map);
    return new ModelSpecification(
        sourceIdentifier,
        name,
        facetsBuilder.build(),
        scalar,
        compound,
        collection,
        map,
        reference);
  }

  public ModelSpecificationBuilder copyOf(ModelSpecification other) {

    return new ModelSpecificationBuilder(other.getSourceIdentifier())
        .name(other.getName())
        .scalarModel(other.getScalar().orElse(null))
        .referenceModel(other.getReference().orElse(null))
        .compoundModel(other.getCompound().orElse(null))
        .collectionModel(other.getCollection().orElse(null))
        .mapModel(other.getMap().orElse(null))
        .facetsBuilder()
        .copyOf(other.getFacets())
        .yield();
  }

  private void ensureValidSpecification(
      Object... specs) {
    long specCount = Arrays.stream(specs)
        .filter(Objects::nonNull)
        .count();
    if (specCount == 0) {
      throw new IllegalArgumentException("At least one type of specification is required");
    }
    if (specCount > 1) {
      throw new IllegalArgumentException("Only one of the specifications should be non null");
    }
  }
}