package com.mangofactory.documentation.spring.web.mixins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder

class DocumentationContextSupport {

  @SuppressWarnings("GrMethodMayBeStatic")
  DocumentationContextBuilder defaultContextBuilder() {
    new DocumentationContextBuilder()
            .withHandlerMappings([])
            .typeResolver(new TypeResolver())
  }

}
