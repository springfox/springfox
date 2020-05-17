package spring.documentation.oas

import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import springfox.documentation.schema.AlternateTypesSupport
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ModelSpecification
import springfox.documentation.service.ModelNamesRegistry
import springfox.documentation.spring.web.scanners.DefaultModelNamesRegistryFactory
import springfox.documentation.spring.web.scanners.ModelSpecificationRegistryBuilder

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

trait ModelRegistrySupport extends AlternateTypesSupport {
  def modelRegistryBuilder = new ModelSpecificationRegistryBuilder()
  @Shared
  def resolver = new TypeResolver()
  @Shared
  def namingStrategy = new DefaultGenericTypeNamingStrategy()

  ModelNamesRegistry modelNamesRegistry(ModelSpecification... specifications) {
    modelNamesRegistry(Arrays.asList(specifications))
  }

  ModelNamesRegistry modelNamesRegistry(Collection<ModelSpecification> specifications) {
    specifications.each {
      modelRegistryBuilder.add(it)
    }
    new DefaultModelNamesRegistryFactory().modelNamesRegistry(modelRegistryBuilder.build())
  }

  def requestResponseAndNamesRegistry(provider, type) {
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
