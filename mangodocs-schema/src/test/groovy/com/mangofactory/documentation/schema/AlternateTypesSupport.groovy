package com.mangofactory.documentation.schema

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.spi.schema.AlternateTypeProvider
import com.mangofactory.documentation.spi.service.contexts.Defaults

class AlternateTypesSupport {

  def defaultRules(TypeResolver resolver = new TypeResolver()) {
    new Defaults().defaultRules(resolver);
  }

  AlternateTypeProvider alternateTypeProvider() {
    new AlternateTypeProvider(defaultRules())
  }
}
