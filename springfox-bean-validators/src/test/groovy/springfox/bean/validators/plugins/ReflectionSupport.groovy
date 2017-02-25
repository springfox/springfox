package springfox.bean.validators.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import springfox.documentation.schema.property.field.FieldProvider


trait ReflectionSupport {
  ResolvedField named(clazz, String name) {
    def resolver = new TypeResolver()
    FieldProvider fieldProvider = new FieldProvider(resolver)
    for (ResolvedField field : fieldProvider.in(resolver.resolve(clazz))) {
      if (field.name == name) {
        return field
      }
    }
  }
}
