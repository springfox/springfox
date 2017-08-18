package springfox.documentation.schema

import com.fasterxml.classmate.ResolvedType
import spock.lang.Specification
import springfox.documentation.spi.schema.contexts.ModelContext

import java.lang.reflect.Method
import java.lang.reflect.Modifier

class DefaultModelDependencyProviderSpec extends Specification {
  def "DefaultModelDependencyProvider.resolvedPropertiesAndFields(ModelContext, ResolvedType) should be overridable" (){
    given:
      Method mapPropertiesMethod = DefaultModelDependencyProvider.class.getDeclaredMethod("resolvedPropertiesAndFields", ModelContext.class, ResolvedType.class);
    when:
      def modifiers = mapPropertiesMethod.getModifiers();
    then:
      Modifier.isProtected(modifiers);
  }
}
