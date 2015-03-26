package springfox.documentation.swagger.configuration
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import spock.lang.Specification

import static com.google.common.collect.Lists.*

class JacksonSwaggerSupportSpec extends Specification {

  def "Should register swagger module and obtain object mapper"() {
    given:
      JacksonSwaggerSupport sut = new JacksonSwaggerSupport()
      ApplicationEventPublisher eventPublisher = Mock(ApplicationEventPublisher)
      sut.setApplicationEventPublisher(eventPublisher)
      ObjectMapper objectMapper = Mock(ObjectMapper)
      MappingJackson2HttpMessageConverter jacksonMessageConverter = Mock(MappingJackson2HttpMessageConverter)
      jacksonMessageConverter.getObjectMapper() >> objectMapper

    when:
      sut.configureMessageConverters(newArrayList(jacksonMessageConverter))
    then:
      1 * objectMapper.registerModule(_)
      1 * eventPublisher.publishEvent(_)
  }

  def "Should register swagger module when no message converter exists"() {
    given:
      JacksonSwaggerSupport sut = new JacksonSwaggerSupport()
      ApplicationEventPublisher eventPublisher = Mock(ApplicationEventPublisher)
      sut.setApplicationEventPublisher(eventPublisher)
      ObjectMapper objectMapper = Mock(ObjectMapper)
      MappingJackson2HttpMessageConverter jacksonMessageConverter = Mock(MappingJackson2HttpMessageConverter)
      jacksonMessageConverter.getObjectMapper() >> objectMapper

    when:
      sut.configureMessageConverters(newArrayList())
    then:
      0 * objectMapper.registerModule(_)
      1 * eventPublisher.publishEvent(_)
  }
}
