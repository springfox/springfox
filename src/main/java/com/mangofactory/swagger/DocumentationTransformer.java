package com.mangofactory.swagger;

import com.google.common.annotations.VisibleForTesting;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;

import java.util.Collections;
import java.util.Comparator;

public abstract class DocumentationTransformer {
    private Comparator<DocumentationEndPoint> endPointComparator;
    private Comparator<DocumentationOperation> operationComparator;

    public DocumentationTransformer(EndpointComparator endPointComparator, OperationComparator operationComparator) {
        this.endPointComparator = endPointComparator;
        this.operationComparator = operationComparator;
    }

    /***
     * This is an extensibility point that allows post-processing of the generated documentation
     * Overriding this method allows a documentation object elements to be sorted based on customer requirements
     * @param transformed After a documentation is transformed the new documentation model can be sorted
     * @return returns a document with APIs sorted based on provided comparator. It also sorts operations based on
     * provided comparator.
     *
     * The simplest form of extensibility is providing an Comparator&lt;DocumentationEndpoint&gt; and/or
     * a Comparator&lt;DocumentationOperation&gt; via the swagger configuration extensions
     */
    public Documentation applySorting(Documentation transformed) {
        if (endPointComparator != null && transformed.getApis() != null) {
            Collections.sort(transformed.getApis(), endPointComparator);
            for (DocumentationEndPoint endpoint : transformed.getApis()) {
                if (operationComparator != null && endpoint.getOperations() != null) {
                    Collections.sort(endpoint.getOperations(), operationComparator);
                }
            }
        }
        return transformed;
    }

    /***
     * This is an extensibility point that allows post-processing of the generated documentation
     * Overriding this method allows a documentation object to be transformed
     * @param documentation The input is the default documentation that is generated
     * @return transformed documentation object
     * For e.g. this extensibility can be used to group operations in a different way rather than the default
     * controller based grouping.
     */
    public abstract Documentation applyTransformation(Documentation documentation);

    @VisibleForTesting
    void setEndPointComparator(Comparator<DocumentationEndPoint> endPointComparator) {
        this.endPointComparator = endPointComparator;
    }

    @VisibleForTesting
    void setOperationComparator(Comparator<DocumentationOperation> operationComparator) {
        this.operationComparator = operationComparator;
    }
}
