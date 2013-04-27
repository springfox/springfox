package com.mangofactory.swagger;

import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;

public class DefaultDocumentationTransformer extends DocumentationTransformer {

    @Autowired(required = false)
    public DefaultDocumentationTransformer(Comparator<DocumentationEndPoint> endPointComparator,
                                           Comparator<DocumentationOperation> operationComparator) {
        super(endPointComparator, operationComparator);
    }

    @Override
    public Documentation applyTransformation(Documentation documentation) {
       return documentation;
    }
}
