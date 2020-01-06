package springfox.documentation.spring.web

import org.springframework.mock.env.MockEnvironment
import spock.lang.Specification
import spock.lang.Unroll

class DescriptionResolverSpec extends Specification {
  def properties = ["key1": "key1value", "key2": "key2value"]

  @Unroll
  def "Resolves the #key"() {
    given:
      def env = new MockEnvironment()
      properties.each { env.withProperty(it.key, it.value) }
    and:
      def reader = new DescriptionResolver(env)
    expect:
      value == reader.resolve(key)
    where:
      value         | key
      "key1value"   | '${key1}'
      "key2value"   | '${key2}'
      "key2value"   | '${key2:}'
      "key2value"   | '${key2:key2default}'
      "key3default" | '${key3:key3default}'
      ""            | '${key3:}'
      '${unknown}'  | '${unknown}'
      "key1"        | 'key1'
      "key2"        | 'key2'
      "unknown"     | 'unknown'
  }


}
