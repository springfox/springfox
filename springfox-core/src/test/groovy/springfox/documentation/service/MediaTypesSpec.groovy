package springfox.documentation.service

import org.springframework.http.MediaType
import spock.lang.Specification

class MediaTypesSpec extends Specification {
  def "Cannot instantiate this class"() {
    when:
      new MediaTypes()
    then:
      thrown(UnsupportedOperationException)
  }

  def "Parses known media types"() {
    given:
      def mediaTypes = [MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE] as Set
    when:
      def parsed = MediaTypes.toMediaTypes(mediaTypes)
    then:
      parsed.size() == 2
      parsed.contains(MediaType.APPLICATION_JSON)
      parsed.contains(MediaType.APPLICATION_ATOM_XML)
  }

  def "Preserves the set"() {
    given:
      def mediaTypes = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE] as Set
    when:
      def parsed = MediaTypes.toMediaTypes(mediaTypes)
    then:
      parsed.size() == 1
      parsed.contains(MediaType.APPLICATION_JSON)
      !parsed.contains(MediaType.APPLICATION_ATOM_XML)
  }

  def "Unknown media types are ignored"() {
    given:
      def mediaTypes = ["Hello world", MediaType.APPLICATION_JSON_VALUE] as Set
    when:
      def parsed = MediaTypes.toMediaTypes(mediaTypes)
    then:
      parsed.size() == 1
      parsed.contains(MediaType.APPLICATION_JSON)
  }
}
