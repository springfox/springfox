package com.mangofactory.swagger;

import com.wordnik.swagger.core.Documentation;


public class DefaultDocumentationTransformer extends DocumentationTransformer {

    public DefaultDocumentationTransformer(EndpointComparator endPointComparator,
                                           OperationComparator operationComparator) {
        super(endPointComparator, operationComparator);
    }

    @Override
    public Documentation applyTransformation(Documentation documentation) {
       return documentation;
    }
}
