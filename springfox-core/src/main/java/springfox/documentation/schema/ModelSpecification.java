package springfox.documentation.schema;

import java.util.Optional;

public class ModelSpecification {
  private final ScalarModelSpecification scalar;
  private final CompoundModelSpecification compound;
  private final CollectionSpecification collection;
  private final MapSpecification map;
  private final ReferenceModelSpecification reference;
  private final ModelFacets facets;
  private final String sourceIdentifier;
  private final String name;

  @SuppressWarnings("ParameterNumber")
  public ModelSpecification(
      String sourceIdentifier,
      String name,
      ModelFacets facets,
      ScalarModelSpecification scalar,
      CompoundModelSpecification compound,
      CollectionSpecification collection,
      MapSpecification map,
      ReferenceModelSpecification reference) {
    this.sourceIdentifier = sourceIdentifier;
    this.name = name;
    this.collection = collection;
    this.facets = facets;
    this.map = map;
    this.scalar = scalar;
    this.compound = compound;
    this.reference = reference;
  }

  public String getSourceIdentifier() {
    return sourceIdentifier;
  }

  public Optional<ScalarModelSpecification> getScalar() {
    return Optional.ofNullable(scalar);
  }

  public Optional<CompoundModelSpecification> getCompound() {
    return Optional.ofNullable(compound);
  }

  public Optional<CollectionSpecification> getCollection() {
    return Optional.ofNullable(collection);
  }

  public Optional<MapSpecification> getMap() {
    return Optional.ofNullable(map);
  }

  public Optional<ReferenceModelSpecification> getReference() {
    return Optional.ofNullable(reference);
  }

  public ModelFacets getFacets() {
    return facets;
  }

  public String getName() {
    return name;
  }
}
