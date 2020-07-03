package springfox.documentation.spi.service.contexts


import spock.lang.Specification
import springfox.documentation.OperationNameGenerator
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.service.SecurityReference

import java.util.function.Predicate

import static org.springframework.web.bind.annotation.RequestMethod.*

class SecurityContextSpec extends Specification {
  def "Prefers operation selector"() {
    given:
    def sut = new SecurityContextBuilder()
        .securityReferences([Mock(SecurityReference)])
        .forHttpMethods(methodSelector)
        .forPaths(pathSelector)
        .operationSelector(operationSelector)
        .build()

    when:
    def references = sut.securityForOperation(new OperationContext(
        new OperationBuilder(Mock(OperationNameGenerator)),
        PUT,
        Mock(RequestMappingContext),
        0))

    then:
    references.size()

    where:
    methodSelector   | pathSelector     | operationSelector | expectedReferences
    null             | null             | null              | 1
    truePredicate()  | null             | null              | 1
    falsePredicate() | null             | null              | 0
    null             | truePredicate()  | null              | 1
    truePredicate()  | truePredicate()  | null              | 1
    falsePredicate() | truePredicate()  | null              | 0
    null             | falsePredicate() | null              | 1
    truePredicate()  | falsePredicate() | null              | 1
    falsePredicate() | falsePredicate() | null              | 0
    null             | null             | truePredicate()   | 1
    truePredicate()  | null             | truePredicate()   | 1
    falsePredicate() | null             | truePredicate()   | 1
    null             | truePredicate()  | truePredicate()   | 1
    truePredicate()  | truePredicate()  | truePredicate()   | 1
    falsePredicate() | truePredicate()  | truePredicate()   | 1
    null             | falsePredicate() | truePredicate()   | 1
    truePredicate()  | falsePredicate() | truePredicate()   | 1
    falsePredicate() | falsePredicate() | truePredicate()   | 1
    null             | null             | falsePredicate()  | 0
    truePredicate()  | null             | falsePredicate()  | 0
    falsePredicate() | null             | falsePredicate()  | 0
    null             | truePredicate()  | falsePredicate()  | 0
    truePredicate()  | truePredicate()  | falsePredicate()  | 0
    falsePredicate() | truePredicate()  | falsePredicate()  | 0
    null             | falsePredicate() | falsePredicate()  | 0
    truePredicate()  | falsePredicate() | falsePredicate()  | 0
    falsePredicate() | falsePredicate() | falsePredicate()  | 0
  }

  private Predicate<?> truePredicate() {
    { each -> true }
  }

  private Predicate<?> falsePredicate() {
    { each -> true }
  }
}
