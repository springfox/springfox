package springfox.documentation.builders;

import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.schema.ModelFacets;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;

import java.util.Arrays;
import java.util.Objects;

public class ModelSpecificationBuilder {
  private final String sourceIdentifier;
  private String name;
  private ModelFacets facets;
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

  public ModelSpecificationBuilder facets(ModelFacets facets) {
    this.facets = facets;
    return this;
  }

  public ModelSpecificationBuilder scalarModel(ScalarType type) {
    if (type != null) {
      this.scalar = new ScalarModelSpecification(type);
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
        facets,
        scalar,
        compound,
        collection,
        map,
        reference);
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