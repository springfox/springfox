package springfox.documentation.schema;

import java.util.Optional;

public class ModelSpecification {
  private ScalarModelSpecification scalar;
  private CompoundModelSpecification compound;
  private CollectionSpecification collection;
  private MapSpecification map;
  private ReferenceModelSpecification reference;
  private ModelFacets facets;
  private String sourceIdentifier;
  private String name;

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
