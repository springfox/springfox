package com.mangofactory.documentation.spring.web.mixins
import com.fasterxml.classmate.TypeResolver
import com.google.common.collect.Ordering
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder

import static com.mangofactory.documentation.spi.service.contexts.Orderings.nickNameComparator

class DocumentationContextSupport {

  @SuppressWarnings("GrMethodMayBeStatic")
  DocumentationContextBuilder defaultContextBuilder() {
    new DocumentationContextBuilder()
            .handlerMappings([])
            .operationOrdering(Ordering.from(nickNameComparator()))
            .typeResolver(new TypeResolver())
  }

}
