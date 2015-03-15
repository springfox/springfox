package springdox.documentation.schema
import com.fasterxml.classmate.TypeResolver
import springdox.documentation.spi.schema.AlternateTypeProvider
import springdox.documentation.spi.service.contexts.Defaults

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
