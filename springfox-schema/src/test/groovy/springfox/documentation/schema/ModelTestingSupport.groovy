package springfox.documentation.schema

trait ModelTestingSupport {
  def assertPropertySpecification(
      CompoundModelSpecification compoundSpec,
      String propertyName,
      type,
      isRequest = false) {
    if (type instanceof ScalarType) {
      assertScalarPropertySpecification(compoundSpec, propertyName, type)
    } else {
      assertComplexPropertySpecification(
          compoundSpec,
          propertyName,
          isRequest ? requestModelKey(type) : responseModelKey(type))
    }
  }

  def assertScalarPropertySpecification(
      CompoundModelSpecification compoundSpec,
      String propertyName,
      ScalarType scalar) {
    def modelProperty = compoundSpec.properties.find { it.name.equals(propertyName) }
    assert modelProperty != null
    assert modelProperty.type.scalar.isPresent()
    assert scalar.equals(modelProperty.type.scalar.get().type)
    assert !modelProperty.elementFacet(CollectionElementFacet).isPresent()
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
    assert !modelProperty.elementFacet(CollectionElementFacet).isPresent()
    true
  }

  def assertMapPropertySpecification(
      CompoundModelSpecification compoundSpec,
      String propertyName,
      key,
      value,
      isRequest = true) {
    def modelProperty = compoundSpec.properties.find { (it.name == propertyName) }
    assert modelProperty != null
    assert modelProperty.type.map.isPresent()
    if (key instanceof ScalarType) {
      assert modelProperty.type.map.get().key.scalar.isPresent()
      assert key == modelProperty.type.map.get().key.scalar.get().type
    } else {
      def modelKey = isRequest ? requestModelKey(key) : responseModelKey(key)
      assert modelProperty.type.map.get().key.reference.isPresent()
      assert modelKey == modelProperty.type.map.get().key.reference.get().key
    }
    true
  }


  def assertCollectionPropertySpecification(
      CompoundModelSpecification specification,
      String propertyName,
      CollectionType collectionType,
      itemType,
      isRequest = false) {
    def collectionProperty = specification.properties.find { it.name.equals(propertyName) }
    assert !collectionProperty.elementFacet(CollectionElementFacet).isPresent()
    if (itemType instanceof ScalarType) {
      assertScalarCollectionPropertySpecification(
          collectionProperty.type.collection.orElse(null),
          propertyName,
          collectionType,
          itemType)
    } else {
      assertComplexCollectionPropertySpecification(
          collectionProperty.type.collection.orElse(null),
          propertyName,
          collectionType,
          isRequest ? requestModelKey(itemType) : responseModelKey(itemType))
    }
  }

  def assertScalarCollectionPropertySpecification(
      CollectionSpecification collectionSpecification,
      String propertyName,
      CollectionType collectionType,
      ScalarType itemType) {
    assert collectionSpecification != null
    assert collectionSpecification.collectionType == collectionType
    assert collectionSpecification.model.scalar.isPresent()
    def item = collectionSpecification.model.scalar.get()
    assert item.type == itemType
    true
  }

  def assertComplexCollectionPropertySpecification(
      CollectionSpecification collectionSpecification,
      String propertyName,
      CollectionType collectionType,
      itemReference) {
    assert collectionSpecification != null
    assert collectionSpecification.collectionType == collectionType
    assert collectionSpecification.model.reference.isPresent()
    def reference = collectionSpecification.model.reference.get()
    assert reference.key == itemReference
    true
  }

  def responseModelKey(Class<?> type) {
    def alias = Types.typeNameFor(type)
    new ModelKey(
        new QualifiedModelName(
            type.getPackage()?.name ?: "",
            alias ?: type.simpleName),
        null,
        new ArrayList<>(),
        true)
  }

  def requestModelKey(Class<?> type) {
    def alias = Types.typeNameFor(type)
    new ModelKey(
        new QualifiedModelName(
            type.getPackage()?.name ?: "",
            alias ?: type.simpleName),
        null,
        new ArrayList<>(),
        false)
  }
}
