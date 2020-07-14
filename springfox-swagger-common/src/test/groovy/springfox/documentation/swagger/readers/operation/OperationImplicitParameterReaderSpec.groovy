package springfox.documentation.swagger.readers.operation

import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ModelSpecificationBuilder
import springfox.documentation.builders.ReferenceModelSpecificationBuilder
import springfox.documentation.schema.Example
import springfox.documentation.schema.ScalarType
import springfox.documentation.swagger.readers.parameter.ApiImplicitParamAnnotationSupport

class OperationImplicitParameterReaderSpec extends Specification implements ApiImplicitParamAnnotationSupport {
  @Unroll
  def "Implicit params are evaluated correctly"() {
    when:
    def model = OperationImplicitParameterReader.modelSpecification(implicitParamAnnotation)

    then:
    model?.scalar?.orElse(null)?.type == expectedScalar
    model?.reference?.orElse(null) == expectedReference
    model?.collection
        ?.map { c -> c.model }
        ?.orElse(null) == collectionItemSpecification(expectedCollectionType)

    where:
    implicitParamAnnotation                                      | expectedScalar     | expectedCollectionType | expectedReference
    apiImplicitParam()                                           | null               | null                   | null
    apiImplicitParam("string")                                   | ScalarType.STRING  | null                   | null
    apiImplicitParam("string", "byte")                           | ScalarType.BYTE    | null                   | null
    apiImplicitParam("string", "byte", "int")                    | ScalarType.INTEGER | null                   | null
    apiImplicitParam("string", "byte", "int", Long)              | ScalarType.LONG    | null                   | null
    apiImplicitParam("string", "byte", "int", Example)           | null               | null                   | reference(Example)
    collectionApiImplicitParam()                                 | null               | null                   | null
    collectionApiImplicitParam("string")                         | null               | ScalarType.STRING      | null
    collectionApiImplicitParam("string", "byte")                 | null               | ScalarType.BYTE        | null
    collectionApiImplicitParam("string", "byte", "int")          | null               | ScalarType.INTEGER     | null
    collectionApiImplicitParam("string", "byte", "int", Long)    | null               | ScalarType.LONG        | null
    collectionApiImplicitParam("string", "byte", "int", Example) | null               | reference(Example)     | null

  }

  def reference(Class clazz) {
    new ReferenceModelSpecificationBuilder()
        .key { k ->
          k.qualifiedModelName {
            q ->
              q.name(clazz.simpleName)
                  .namespace(clazz.packageName)
          }
        }.build()
  }

  def collectionItemSpecification(type) {
    if (type == null) {
      return null
    }
    if (type instanceof ScalarType) {
      return new ModelSpecificationBuilder()
          .scalarModel(type)
          .build()
    }
    return new ModelSpecificationBuilder()
        .referenceModel {
          r ->
            r.copyOf(type)
        }.build()
  }
}
