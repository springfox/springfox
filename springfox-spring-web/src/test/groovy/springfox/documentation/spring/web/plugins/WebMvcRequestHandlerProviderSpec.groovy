package springfox.documentation.spring.web.plugins

import spock.lang.Specification

class WebMvcRequestHandlerProviderSpec extends Specification {
  def "when handler mappings is empty or null" () {
    given:
      WebMvcRequestHandlerProvider sut = new WebMvcRequestHandlerProvider(handlers)
    expect:
      sut.requestHandlers().size() == 0
    where:
      handlers << [null, []]
  }
}
