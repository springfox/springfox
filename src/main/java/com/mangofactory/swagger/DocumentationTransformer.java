package com.mangofactory.swagger;

import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;

import java.util.Collections;

public abstract class DocumentationTransformer {
    protected final SwaggerConfiguration configuration;

    public DocumentationTransformer(SwaggerConfiguration configuration) {
        this.configuration = configuration;
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
        if (configuration.getEndPointComparator() != null && transformed.getApis() != null) {
            Collections.sort(transformed.getApis(), configuration.getEndPointComparator());
            for (DocumentationEndPoint endpoint : transformed.getApis()) {
                if (configuration.getOperationComparator() != null && endpoint.getOperations() != null) {
                    Collections.sort(endpoint.getOperations(), configuration.getOperationComparator());
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
}
