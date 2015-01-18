package com.mangofactory.documentation.spring.web.mixins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.spi.service.contexts.Defaults
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder

class DocumentationContextSupport {

  @SuppressWarnings("GrMethodMayBeStatic")
  DocumentationContextBuilder defaultContextBuilder() {
    new DocumentationContextBuilder(new Defaults())
            .withHandlerMappings([])
            .typeResolver(new TypeResolver())
  }

}
