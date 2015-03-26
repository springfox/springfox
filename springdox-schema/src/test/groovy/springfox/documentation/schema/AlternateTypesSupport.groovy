package springfox.documentation.schema
import com.fasterxml.classmate.TypeResolver
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.service.contexts.Defaults

class AlternateTypesSupport {

  def defaultRules(TypeResolver resolver = new TypeResolver()) {
    def rules = new Defaults().defaultRules(resolver);
//    rules.add(newMapRule(WildcardType, WildcardType))
    rules
  }

  AlternateTypeProvider alternateTypeProvider() {
    new AlternateTypeProvider(defaultRules())
  }
}
