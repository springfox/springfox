package springfox.documentation.schema

import com.fasterxml.classmate.ResolvedType
import spock.lang.Specification
import springfox.documentation.spi.schema.contexts.ModelContext

import java.lang.reflect.Method
import java.lang.reflect.Modifier

class DefaultModelProviderSpec extends Specification {
  def "DefaultModelProvider.isSkipModelFor(ModelContext, ResolvedType) should be overridable" (){
    given:
      Method mapPropertiesMethod = DefaultModelProvider.class.getDeclaredMethod("isSkipModelFor", ModelContext.class, ResolvedType.class);
    when:
      def modifiers = mapPropertiesMethod.getModifiers();
    then:
      Modifier.isProtected(modifiers);
  }

}
