package springfox.documentation.schema

import spock.lang.Specification

class ModelCacheKeysSpec extends Specification {
  def "Cannot instantiate the ModelCacheKeys class" () {
    when:
      new ModelCacheKeys()
    then:
      thrown(UnsupportedOperationException)
  }
}
