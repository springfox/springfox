package springfox.documentation.schema

trait ModelTestingSupport {
  def assertScalarPropertySpecification(
      CompoundModelSpecification compoundSpec,
      String propertyName,
      ScalarType scalar) {
    def modelProperty = compoundSpec.properties.find { it.name.equals(propertyName) }
    assert modelProperty != null
    assert modelProperty.type.scalar.isPresent()
    assert scalar.equals(modelProperty.type.scalar.get().type)
    assert !modelProperty.facetOfType(CollectionElementFacet).isPresent()
    true
  }

  def assertComplexPropertySpecification(
      CompoundModelSpecification compoundSpec,
      String propertyName,
      modelKey) {
    def modelProperty = compoundSpec.properties.find { it.name.equals(propertyName) }
    assert modelProperty != null
    assert modelProperty.type.reference.isPresent()
    assert modelKey.equals(modelProperty.type.reference.get().key)
    assert !modelProperty.facetOfType(CollectionElementFacet).isPresent()
    true
  }

  def responseModelKey(Class<?> type) {
    def alias = Types.typeNameFor(type)
    new ModelKey(type.getPackage()?.name ?: "", alias ?: type.simpleName, true)
  }

  def requestModelKey(Class<?> type) {
    def alias = Types.typeNameFor(type)
    new ModelKey(type.getPackage()?.name ?: "", alias ?: type.simpleName, false)
  }
}
