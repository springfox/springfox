package springfox.documentation.builders;

import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.schema.ModelFacets;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;

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

  public ModelSpecificationBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ModelSpecificationBuilder withFacets(ModelFacets facets) {
    this.facets = facets;
    return this;
  }

  public ModelSpecificationBuilder withScalar(ScalarModelSpecification scalar) {
    this.scalar = scalar;
    this.compound = null;
    this.collection = null;
    this.map = null;
    this.reference = null;
    return this;
  }

  public ModelSpecificationBuilder withScalar(ScalarType scalar) {
    this.scalar = new ScalarModelSpecification(scalar);
    this.compound = null;
    this.collection = null;
    this.map = null;
    this.reference = null;
    return this;
  }

  public ModelSpecificationBuilder withCompound(CompoundModelSpecification compound) {
    this.compound = compound;
    this.scalar = null;
    this.collection = null;
    this.map = null;
    this.reference = null;
    return this;
  }

  public ModelSpecificationBuilder withCollection(CollectionSpecification collection) {
    this.collection = collection;
    this.scalar = null;
    this.compound = null;
    this.map = null;
    this.reference = null;
    return this;
  }

  public ModelSpecificationBuilder withMap(MapSpecification map) {
    this.map = map;
    this.scalar = null;
    this.compound = null;
    this.collection = null;
    this.reference = null;
    return this;
  }

  public ModelSpecificationBuilder withReference(ReferenceModelSpecification reference) {
    this.reference = reference;
    this.scalar = null;
    this.compound = null;
    this.collection = null;
    this.map = null;
    return this;
  }

  public ModelSpecification build() {
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
}