package springdox.documentation.schema

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Type

import static springdox.documentation.schema.AlternateTypeRules.*

class AlternateTypeRuleSpec extends Specification {
  @Shared TypeResolver resolver = new TypeResolver()

  def "Cannot instantiate the AlternateTypeRules class" () {
    when:
      new AlternateTypeRules()
    then:
      thrown(UnsupportedOperationException)
  }

  def "Can figure out if the rule applies to a given type" () {
    given:
      def sut = newRule(original, alternate) //Alternate doesn't matter for this test
    expect:
      sut.appliesTo(testType) == isApplicable
    where:
      original                          | alternate                 | testType                      | isApplicable
      resolve(Date)                     | resolve(String)           | resolve(Date)                 | true
      resolve(Date)                     | resolve(String)           | resolve(String)               | false
      resolve(List, Date)               | resolve(String)           | resolve(List, Date)           | true
      resolve(List, Date)               | resolve(String)           | resolve(List, String)         | false
      resolve(List, WildcardType)       | resolve(String)           | resolve(List, String)         | true
      resolve(List, WildcardType)       | resolve(String)           | resolve(List, WildcardType)   | true
      listOfListsOfType(WildcardType)   | listOfListsOfType(String) | listOfListsOfType(Date)       |  true
      resolve(WildcardType)             | resolve(String)           | resolve(Date)                 | false
  }

  private ResolvedType listOfListsOfType(Class clazz) {
    resolve(List, resolve(List, clazz))
  }

  def "Rules provide the correct alternate types given a test type" () {
    given:
      def sut = newRule(original, alternate)
    expect:
      sut.alternateFor(testType).equals(expected)
    where:
      original                    | alternate                           | testType                      | expected
      resolve(List, WildcardType) | resolve(Map, String, WildcardType)  | resolve(List, String)         | resolve(Map, String, String)
      resolve(List, WildcardType) | resolve(Map, String, WildcardType)  | resolve(List, Date)           | resolve(Map, String, Date)
      resolve(List, WildcardType) | resolve(Map, String, WildcardType)  | resolve(List, WildcardType)   | resolve(Map, String, WildcardType)
      resolve(List, WildcardType) | resolve(Map, WildcardType, String)  | resolve(List, String)         | resolve(Map, String, String)
      resolve(List, WildcardType) | resolve(Map, WildcardType, String)  | resolve(List, Date)           | resolve(Map, Date, String)
      resolve(List, WildcardType) | resolve(Map, WildcardType, String)  | resolve(List, WildcardType)   | resolve(Map, WildcardType, String)
      resolve(WildcardType)       | resolve(String)                     | resolve(Date)                 | resolve(WildcardType)
      resolve(WildcardType)       | resolve(String)                     | resolve(WildcardType)         | resolve(String)
      resolve(List, WildcardType) | resolve(String)                     | resolve(Date)                 | resolve(List, WildcardType)
      resolve(List, WildcardType) | resolve(WildcardType)               | resolve(List, Date)           | resolve(Date)
  }

  def "Can figure out if Map Rules apply" () {
    given:
      def sut = newMapRule(String, original) //Alternate doesn't matter for this test
    expect:
      sut.appliesTo(resolve(Map, String, testType)) == isApplicable
    where:
      original                 | alternate        | testType             | isApplicable
      Date                     | String           | Date                 | true
      WildcardType             | String           | Date                 | true
      WildcardType             | String           | String               | true
  }

  def "When the shape of the wildcard original type and the test type dont match" () {
    given:
      def sut = newRule(original, alternate)
    expect:
      sut.alternateFor(testType).equals(original)
    where:
      original                    | alternate              | testType
      resolve(List, WildcardType) | resolve(List, String)  | resolve(String)
  }

  def "When the shape of the wildcard original type and the test type matches" () {
    given:
      def sut = newRule(original, alternate)
    expect:
      sut.alternateFor(testType).equals(testType)
    where:
      original                    | alternate              | testType
      resolve(List, WildcardType) | resolve(List, String)  | resolve(List, String)
  }

  def "When the wildcard replacements dont match" () {
    given:
      def sut = newRule(original, alternate)
    expect:
      sut.alternateFor(testType).equals(testType)
    where:
      original                    | alternate              | testType
      resolve(List, WildcardType) | resolve(List, String)  | resolve(List, String)
  }

  ResolvedType resolve(Class clazz, Type ... typeBindings) {
    resolver.resolve(clazz, typeBindings)
  }
}
