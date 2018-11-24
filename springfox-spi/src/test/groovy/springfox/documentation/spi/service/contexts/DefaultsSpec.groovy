package springfox.documentation.spi.service.contexts

import com.fasterxml.classmate.TypeResolver
import org.joda.time.DateMidnight
import org.joda.time.DateTime
import org.joda.time.ReadableDateTime
import org.joda.time.ReadableInstant
import org.springframework.http.HttpHeaders
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.annotations.ApiIgnore
import springfox.documentation.spi.schema.AlternateTypeProvider

import java.time.*

class DefaultsSpec extends Specification {
  @Shared
  def resolver = new TypeResolver()

  def "Verify defaults"() {
    when:
    def sut = new Defaults()

    then:
    sut.defaultIgnorableParameterTypes().containsAll(ignoreableTypes())
    sut.defaultResponseMessages().keySet().containsAll(RequestMethod.values())
    sut.apiDescriptionOrdering() != null
    sut.apiListingReferenceOrdering() != null
    sut.defaultExcludeAnnotations().containsAll([ApiIgnore])
    sut.operationOrdering() != null
  }


  def "Verify default rules"() {
    given:
    def sut = new Defaults()

    when:
    def rules = sut.defaultRules(resolver)
    def evaluator = new AlternateTypeProvider(rules)

    then:
    rules.size() == 19
    evaluator.alternateFor(source) == alternate

    where:
    source                                        | alternate
    resolver.resolve(Map)                         | resolver.resolve(Object)
    resolver.resolve(Map, String, Object)         | resolver.resolve(Object)
    resolver.resolve(Map, Object, Object)         | resolver.resolve(Object)
    resolver.resolve(Map, Object, Object)         | resolver.resolve(Object)
    resolver.resolve(LocalDate)                   | resolver.resolve(java.sql.Date)
    resolver.resolve(LocalDateTime)               | resolver.resolve(Date)
    resolver.resolve(Instant)                     | resolver.resolve(Date)
    resolver.resolve(OffsetDateTime)              | resolver.resolve(Date)
    resolver.resolve(ZonedDateTime)               | resolver.resolve(Date)
    resolver.resolve(org.joda.time.LocalDate)     | resolver.resolve(java.sql.Date)
    resolver.resolve(org.joda.time.LocalDateTime) | resolver.resolve(Date)
    resolver.resolve(org.joda.time.Instant)       | resolver.resolve(Date)
    resolver.resolve(DateTime)                    | resolver.resolve(Date)
    resolver.resolve(ReadableDateTime)            | resolver.resolve(Date)
    resolver.resolve(ReadableInstant)             | resolver.resolve(Date)
    resolver.resolve(DateMidnight)                | resolver.resolve(Date)
  }

  def ignoreableTypes() {
    [Class.class,
     Void.class,
     Void.TYPE,
     HttpHeaders.class,
     BindingResult.class,
     UriComponentsBuilder.class,
     ApiIgnore.class] as Set
  }
}
