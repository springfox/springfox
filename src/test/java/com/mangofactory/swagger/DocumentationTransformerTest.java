package com.mangofactory.swagger;

import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;

import static com.google.common.collect.Lists.newArrayList;

public class DocumentationTransformerTest {
    private DocumentationTransformer transformer;
    private SwaggerConfiguration configuration;
    private SwaggerConfigurationExtension configurationExtension;
    @Before
    public void setUp() throws Exception {
        configuration = new SwaggerConfiguration();
        configurationExtension = new SwaggerConfigurationExtension();
        configuration.setExtensions(configurationExtension);
        transformer = new DocumentationTransformer(configuration) {
            @Override
            public Documentation applyTransformation(Documentation documentation) {
                return documentation;
            }
        };
    }

    private void setupComparators(SwaggerConfigurationExtension extensions) {
        extensions.setEndPointComparator(new Comparator<DocumentationEndPoint>() {
            @Override
            public int compare(DocumentationEndPoint endPoint, DocumentationEndPoint endPoint2) {
                return endPoint.getPath().compareTo(endPoint2.getPath());
            }
        });
        extensions.setOperationComparator(new Comparator<DocumentationOperation>() {
            @Override
            public int compare(DocumentationOperation operation, DocumentationOperation operation2) {
                return operation.getHttpMethod().compareTo(operation2.getHttpMethod());
            }
        });
    }

    @Test
    public void whenApisCollectionIsEmptyAndNoComparators() {
        Documentation documentation = new Documentation();
        transformer.applySorting(documentation);
    }

    @Test
    public void whenApisCollectionHasOneElementAndEmptyOperationsCollectionAndNoComparators() {
        Documentation documentation = new Documentation();
        documentation.setApis(newArrayList(new DocumentationEndPoint("/somepath", "some desc")));
        transformer.applySorting(documentation);
    }

    @Test
    public void whenApisCollectionHasOneElementWithNonEmptyOperationsCollectionAndNoComparators() {
        Documentation documentation = new Documentation();
        DocumentationEndPoint endPoint = new DocumentationEndPoint("/somepath", "some desc");
        endPoint.setOperations(newArrayList(new DocumentationOperation("GET", "some method", "some method")));
        documentation.setApis(newArrayList(endPoint));
        transformer.applySorting(documentation);
    }

    @Test
    public void whenApisCollectionIsEmpty() {
        setupComparators(configurationExtension);
        Documentation documentation = new Documentation();
        transformer.applySorting(documentation);
    }

    @Test
    public void whenApisCollectionHasOneElementAndEmptyOperationsCollection() {
        setupComparators(configurationExtension);
        Documentation documentation = new Documentation();
        documentation.setApis(newArrayList(new DocumentationEndPoint("/somepath", "some desc")));
        transformer.applySorting(documentation);
    }

    @Test
    public void whenApisCollectionHasOneElementWithNonEmptyOperationsCollection() {
        setupComparators(configurationExtension);
        Documentation documentation = new Documentation();
        DocumentationEndPoint endPoint = new DocumentationEndPoint("/somepath", "some desc");
        endPoint.setOperations(newArrayList(new DocumentationOperation("GET", "some method", "some method")));
        documentation.setApis(newArrayList(endPoint));
        transformer.applySorting(documentation);
    }
}
