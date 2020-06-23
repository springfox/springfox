package springfox.documentation.spring.web.scanners

import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ModelSpecification
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.service.ModelNamesRegistry
import springfox.documentation.spring.web.dummy.Address
import springfox.documentation.spring.web.dummy.ModelWithSameNameClasses

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ModelNamesRegistrySpec
    extends Specification
    implements ModelProviderSupport {
  def sut = new ModelSpecificationRegistryBuilder()
  @Shared
  def resolver = new TypeResolver()
  @Shared
  def namingStrategy = new DefaultGenericTypeNamingStrategy()

  def "Request and response types are detected correctly"() {
    given:
    def provider = defaultModelSpecificationProvider()
    def type = ModelWithSameNameClasses

    when:
    def (asInput, asReturn, modelNamesRegistry) = requestResponseAndNamesRegistry(provider, type)

    then:
    modelNamesRegistry.modelsByName().keySet().size() == 8
    modelNamesRegistry.nameByKey(asInput.compound.get().modelKey).get() == ModelWithSameNameClasses.simpleName + "Req"
    modelNamesRegistry.nameByKey(asReturn.compound.get().modelKey).get() == ModelWithSameNameClasses.simpleName + "Res"
  }

  def "Handles recursive types"() {
    given:
    def provider = defaultModelSpecificationProvider()
    def type = Address

    when:
    def (asInput, asReturn, modelNamesRegistry) = requestResponseAndNamesRegistry(provider, type)

    then:
    modelNamesRegistry.modelsByName().keySet().size() == 4
    modelNamesRegistry.nameByKey(asInput.compound.get().modelKey).get() == Address.simpleName
    modelNamesRegistry.nameByKey(asReturn.compound.get().modelKey).get() == Address.simpleName
  }


  ModelNamesRegistry modelNamesRegistry(ModelSpecification... specifications) {
    modelNamesRegistry(Arrays.asList(specifications))
  }

  ModelNamesRegistry modelNamesRegistry(Collection<ModelSpecification> specifications) {
    specifications.each {
      sut.add(it)
    }
    new DefaultModelNamesRegistryFactory().modelNamesRegistry(sut.build())
  }

  def requestResponseAndNamesRegistry(
      provider,
      type) {
    def inputParam = inputParam(
        "0_0",
        "group",
        resolver.resolve(type),
        Optional.empty(),
        new HashSet<>(),
        OAS_30,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    def returnParam = returnValue(
        "0_0",
        "group",
        resolver.resolve(type),
        Optional.empty(),
        OAS_30,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    ModelSpecification asInput = provider.modelSpecificationsFor(
        inputParam).get()
    def inputDependencies = provider.modelDependenciesSpecifications(inputParam)

    ModelSpecification asReturn = provider.modelSpecificationsFor(
        returnParam).get()
    def returnDependencies = provider.modelDependenciesSpecifications(returnParam)

    def models = [asInput, asReturn]
    models.addAll(inputDependencies)
    models.addAll(returnDependencies)

    [asInput, asReturn, modelNamesRegistry(models)]
  }
}
