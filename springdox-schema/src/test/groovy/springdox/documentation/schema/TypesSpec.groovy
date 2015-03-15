package springdox.documentation.schema

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification

class TypesSpec extends Specification {
  def "detects void type" () {
    given:
      def typeResolver = new TypeResolver()
    expect:
      Types.isVoid(typeResolver.resolve(type)) == isVoid
    where:
      type        | isVoid
      Void.class  | true
      Void.TYPE   | true
      Integer.TYPE| false
  }
}
