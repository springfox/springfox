package springfox.documentation.schema;

import springfox.documentation.service.VendorExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import static java.util.Collections.*;

public class ModelSpecification {
  private final ScalarModelSpecification scalar;
  private final CompoundModelSpecification compound;
  private final CollectionSpecification collection;
  private final MapSpecification map;
  private final ReferenceModelSpecification reference;
  private final ModelFacets facets;
  private final String name;

  @SuppressWarnings("ParameterNumber")
  public ModelSpecification(
      String name,
      ModelFacets facets,
      ScalarModelSpecification scalar,
      CompoundModelSpecification compound,
      CollectionSpecification collection,
      MapSpecification map,
      ReferenceModelSpecification reference) {
    this.name = name;
    this.collection = collection;
    this.facets = facets;
    this.map = map;
    this.scalar = scalar;
    this.compound = compound;
    this.reference = reference;
  }

  public Optional<ModelKey> key() {
    return getCompound()
        .map(CompoundModelSpecification::getModelKey);
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

  public Optional<ModelFacets> getFacets() {
    return Optional.ofNullable(facets);
  }

  @SuppressWarnings("unchecked")
  public List<Example> getFacetExamples() {
    return getFacets().map(ModelFacets::getExamples).orElse(EMPTY_LIST);
  }

  @SuppressWarnings("unchecked")
  public List<VendorExtension> getFacetExtensions() {
    return getFacets().map(ModelFacets::getExtensions).orElse(EMPTY_LIST);
  }

  public String getName() {
    return name;
  }

  @SuppressWarnings("CyclomaticComplexity")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelSpecification that = (ModelSpecification) o;
    return Objects.equals(scalar, that.scalar) &&
        Objects.equals(compound, that.compound) &&
        Objects.equals(collection, that.collection) &&
        Objects.equals(map, that.map) &&
        Objects.equals(reference, that.reference) &&
        Objects.equals(facets, that.facets) &&
        Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        scalar,
        compound,
        collection,
        map,
        reference,
        facets,
        name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ModelSpecification.class.getSimpleName() + "[", "]")
        .add("scalar=" + scalar)
        .add("compound=" + compound)
        .add("collection=" + collection)
        .add("map=" + map)
        .add("reference=" + reference)
        .add("facets=" + facets)
        .add("name='" + name + "'")
        .toString();
  }
}
