package com.mangofactory.swagger;

import com.wordnik.swagger.core.Documentation;

class DefaultDocumentationTransformer extends DocumentationTransformer {

    public DefaultDocumentationTransformer(SwaggerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Documentation applyTransformation(Documentation documentation) {
       return documentation;
    }
}
