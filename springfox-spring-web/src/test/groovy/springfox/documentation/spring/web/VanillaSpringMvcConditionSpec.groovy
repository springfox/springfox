package springfox.documentation.spring.web

import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotationMetadata
import spock.lang.Specification
import spock.lang.Unroll

class VanillaSpringMvcConditionSpec extends Specification {

  @Unroll
  def "detects specified class (SpringApplication.class = #isFound) is present " (){
    given:
      def context = Mock(ConditionContext)
      def metadata = Mock(AnnotationMetadata)
      def sut = bootCondition(isFound)

    expect:
      !isFound == sut.matches(context, metadata)

    where:
      isFound << [true, false]
  }

  def bootCondition(isFound) {
    new VanillaSpringMvcCondition() {
      @Override
      Class<?> classByName(ConditionContext context, String clazz) throws ClassNotFoundException {
        if (isFound) {
          return String
        }
        throw new ClassNotFoundException();
      }
    }
  }
}
